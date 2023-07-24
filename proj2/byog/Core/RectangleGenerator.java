package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class RectangleGenerator {
    public static void generator(int length, int width, Position startPoint, TETile[][] world) {
        for (int x = startPoint.xCoordinate; x < startPoint.xCoordinate + length; x++) {
            for (int y = startPoint.yCoordinate; y < startPoint.yCoordinate + width; y++) {
                Position pos = new Position(x, y);
                if ((x == startPoint.xCoordinate) ||
                        (x == startPoint.xCoordinate + length - 1) ||
                        (y == startPoint.yCoordinate) ||
                        (y == startPoint.yCoordinate + width - 1)) {
                    pos.addBuilding(world, Tileset.WALL);
                } else {
                    pos.addBuilding(world, Tileset.FLOOR);
                }
            }
        }
    }

    public static boolean blankArea(int length, int width, Position startPoint) {
        if (startPoint.xCoordinate + length >= startPoint.WIDTH ||
                startPoint.yCoordinate + width >= startPoint.HEIGHT) {
            return false;
        }
        for (int x = startPoint.xCoordinate; x < startPoint.xCoordinate + length; x++) {
            for (int y = startPoint.yCoordinate; y < startPoint.yCoordinate + width; y++) {
                if (new Position(x, y).isOccupied()) {
                    return false;
                }
            }
        }
        return true;
    }
}
