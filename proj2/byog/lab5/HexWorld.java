package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 60;
    private static final TETile[][] world = new TETile[WIDTH][HEIGHT];
    private static Random rand = new Random();
    private static TETile[] tiles = {Tileset.WALL, Tileset.FLOWER, Tileset.GRASS, Tileset.MOUNTAIN, Tileset.SAND,
    Tileset.TREE, Tileset.WATER};

    private static void addHexagonHelper(int size, int xCoordinate, int yCoordinate, int direction, TETile tile) {
        int x, y;
        for (int i = 0; i < size; i++) {
            x = xCoordinate - i;
            y = yCoordinate + i * direction;
            for (int j = 0; j < size + 2 * i; j++) {
                world[x + j][y] = tile;
            }
        }
    }

    public static void addHexagon(int size, int xCoordinate, int yCoordinate, TETile tile) {
        addHexagonHelper(size, xCoordinate, yCoordinate, -1, tile);
        addHexagonHelper(size, xCoordinate, yCoordinate - 2 * size + 1, 1, tile);
    }

    public static void addBigHexagons(int size) {
        int xCoordinate = 39;
        int yCoordinate = 45;
        int count = 5;
        int[] xOffSet = {-2, -1, 0, 1, 2};
        int x, y;
        for (int xOff : xOffSet) {
            x = xCoordinate + (2 * size - 1) * xOff;
            int yOff;
            if (xOff < 0) {
                yOff = -xOff;
            } else {
                yOff = xOff;
            }
            y = yCoordinate - yOff * size;
            for (int n = 0; n < count - yOff; n++) {
                addHexagon(size, x, y - 2 * size * n, tiles[rand.nextInt(tiles.length)]);
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        /* Add a hexagon of size 5 */
        // addHexagon(5, 10, 10, Tileset.WALL);

        /* Add 19 hexagons of size 3 */
        addBigHexagons(3);
        ter.renderFrame(world);
    }
}
