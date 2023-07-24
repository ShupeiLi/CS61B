package byog.Core;

import byog.TileEngine.TETile;

public interface PositionRecord {
    /* Feel free to change the width and height. */
    int WIDTH = 50;
    int HEIGHT = 30;
    int[][] isOccupiedArray = new int[WIDTH][HEIGHT];
    int[][] isFixedArray = new int[WIDTH][HEIGHT];
}
