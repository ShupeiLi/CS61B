package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game implements PositionRecord {
    TERenderer ter = new TERenderer();

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (TETile[] tiles : world) {
            Arrays.fill(tiles, Tileset.NOTHING);
        }
        SpaceTree tree = new SpaceTree(23416);
        tree.mazeGenerator(world);
        for (int i = HEIGHT - 1; i >=0; i--) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(world[j][i].character());
            }
            System.out.println();
        }
        ter.renderFrame(world);
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (TETile[] tiles : world) {
            Arrays.fill(tiles, Tileset.NOTHING);
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);
        String seedStr;
        if (matcher.find()) {
            seedStr = input.substring(matcher.start(), matcher.end());
        } else {
            throw new RuntimeException("Input should contain a random seed.");
        }
        long seed = Long.parseLong(seedStr);

        SpaceTree tree = new SpaceTree(seed);
        tree.mazeGenerator(world);
        return world;
    }
}
