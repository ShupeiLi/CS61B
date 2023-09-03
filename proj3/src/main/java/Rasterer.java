import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    private final double[] depthLonDPP = new double[7];
    private final double DELTA = MapServer.ROOT_LRLON - MapServer.ROOT_ULLON;

    public Rasterer() {
        for (int i = 0; i < depthLonDPP.length; i++) {
            depthLonDPP[i] = Rasterer.computeLonDPP(
                    MapServer.ROOT_ULLON + DELTA / Math.pow(2, i),
                    MapServer.ROOT_ULLON,
                    MapServer.TILE_SIZE
                    );
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     *
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     *
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified: <br>
     * "render_grid"   : String[][], the files to display. <br>
     * "raster_ul_lon" : Number, the bounding upper left longitude of the rastered image. <br>
     * "raster_ul_lat" : Number, the bounding upper left latitude of the rastered image. <br>
     * "raster_lr_lon" : Number, the bounding lower right longitude of the rastered image. <br>
     * "raster_lr_lat" : Number, the bounding lower right latitude of the rastered image. <br>
     * "depth"         : Number, the depth of the nodes of the rastered image <br>
     * "query_success" : Boolean, whether the query was able to successfully complete; don't
     *                    forget to set this to true on success! <br>
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        double qLrLon = params.get("lrlon");
        double qUlLon = params.get("ullon");
        double qLrLat = params.get("lrlat");
        double qUlLat = params.get("ullat");
        Map<String, Object> results = new HashMap<>();

        // Validate parameters.
        if ((qLrLon <= MapServer.ROOT_ULLON)
                || (qLrLat >= MapServer.ROOT_ULLAT)
                || (qUlLon >= MapServer.ROOT_LRLON)
                || (qUlLat <= MapServer.ROOT_LRLAT)
                || (qUlLon >= qLrLon)
                || (qUlLat <= qLrLat)) {
            Random rand = new Random();
            results.put("render_grid", null);
            results.put("raster_ul_lon", rand.nextDouble());
            results.put("raster_ul_lat", rand.nextDouble());
            results.put("raster_lr_lon", rand.nextDouble());
            results.put("raster_lr_lat", rand.nextDouble());
            results.put("depth", 0);
            results.put("query_success", false);
            return results;
        }

        double userLonDPP = Rasterer.computeLonDPP(
                qLrLon,
                qUlLon,
                params.get("w")
        );
        int depth = selectResolution(userLonDPP);
        double oneImgLength = DELTA / Math.pow(2, depth);
        double oneImgWidth = (MapServer.ROOT_ULLAT - MapServer.ROOT_LRLAT) / Math.pow(2, depth);

        // Row start
        int rUlLonNum = (int) Math.floor((qUlLon - MapServer.ROOT_ULLON) / oneImgLength);
        double rUlLon = MapServer.ROOT_ULLON + oneImgLength * rUlLonNum;
        int rLrLonNum = (int) Math.floor((MapServer.ROOT_LRLON - qLrLon) / oneImgLength);
        double rLrLon = MapServer.ROOT_LRLON - oneImgLength * rLrLonNum;
        // Column start
        int rUlLatNum = (int) Math.floor((MapServer.ROOT_ULLAT - qUlLat) / oneImgWidth);
        double rUlLat = MapServer.ROOT_ULLAT - oneImgWidth * rUlLatNum;
        int rLrLatNum = (int) Math.floor((qLrLat - MapServer.ROOT_LRLAT) / oneImgWidth);
        double rLrLat = MapServer.ROOT_LRLAT + oneImgWidth * rLrLatNum;

        int rowEnd = (int) (Math.pow(2, depth) - rLrLonNum); // Exclusive
        int colEnd = (int) (Math.pow(2, depth) - rLrLatNum);
        String[][] grid = new String[colEnd - rUlLatNum][rowEnd - rUlLonNum];
        for (int j = 0; j < (colEnd - rUlLatNum); j++) {
            for (int i = 0; i < (rowEnd - rUlLonNum); i++) {
                grid[j][i] = "d" + depth + "_x" + (i + rUlLonNum) + "_y" + (j + rUlLatNum) + ".png";
            }
        }

        results.put("render_grid", grid);
        results.put("raster_ul_lon", rUlLon);
        results.put("raster_ul_lat", rUlLat);
        results.put("raster_lr_lon", rLrLon);
        results.put("raster_lr_lat", rLrLat);
        results.put("depth", depth);
        results.put("query_success", true);
        return results;
    }

    /** Compute the longitudinal distance per pixel (LonDPP). */
    private static double computeLonDPP(double lrLon, double ulLon, double width) {
        return (lrLon - ulLon) / width;
    }

    /** Select the suitable resolution. */
    private int selectResolution(double userLonDPP) {
        for (int i = 0; i < depthLonDPP.length - 1; i++) {
            if ((i == 0) && (userLonDPP >= depthLonDPP[i])) {
                return 0;
            }
            if ((userLonDPP < depthLonDPP[i]) && (userLonDPP >= depthLonDPP[i + 1])) {
                return i + 1;
            }
        }
        return 7;
    }
}
