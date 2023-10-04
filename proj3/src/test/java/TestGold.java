import java.util.List;

public class TestGold {

    private static final String OSM_DB_PATH = "../library-sp18/data/berkeley-2018.osm.xml";
    private static GraphDB graph = new GraphDB(OSM_DB_PATH);

    public static void main(String[] args) {
       List<String> list = GraphDB.getLocationsByPrefix("cro");
       for (String s: list) {
           System.out.println(s);
       }
       System.out.println(GraphDB.getLocations("sonoma coffee caf"));
       System.out.println(GraphDB.getLocations("express"));
    }
}
