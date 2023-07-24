package byog.Core;

import byog.TileEngine.TETile;

public class Position implements PositionRecord {
    int xCoordinate;
    int yCoordinate;
    int xDirection;
    int yDirection;

    public Position(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Position(int xCoordinate, int yCoordinate, int xDirection, int yDirection) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.xDirection = xDirection;
        this.yDirection = yDirection;
    }

    public boolean isOccupied() {
        return isOccupiedArray[xCoordinate][yCoordinate] == 1;
    }

    public boolean isFixed() {
        return isFixedArray[xCoordinate][yCoordinate] == 1;
    }

    public void addBuilding(TETile[][] world, TETile tileType) {
        isOccupiedArray[xCoordinate][yCoordinate] = 1;
        world[xCoordinate][yCoordinate] = tileType;
    }
}
