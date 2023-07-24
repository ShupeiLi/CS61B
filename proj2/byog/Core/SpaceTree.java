package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;
import static java.lang.Math.max;

public class SpaceTree implements PositionRecord {
    private final long seed;
    private int randomOffset = 0;
    private final int HALLWAY_LENGTH_UPPER = shorterBoarder() / 5;

    public SpaceTree(long seed) {
        this.seed = seed;
    }

    private class Tree {
        Position pos;
        Position[] doors = new Position[2];
        int doorsPtr = 0;
        Tree parent = null;
        // public Tree siblings = null;
        Tree child = null;
        int length, width;
        boolean isRoom;
        boolean fail = true;

        Tree(Position pos) {
            this.pos = pos;
        }
    }

    /* Sample positions on boarders of the building to create entrances. */
    private void selectDoor(Tree tree, TETile[][] world) {
        double P = 0.5;
        int length = tree.length;
        int width = tree.width;
        Position startPoint = tree.pos;
        while (tree.doorsPtr < tree.doors.length) {
            for (int x = startPoint.xCoordinate; x < startPoint.xCoordinate + length; x++) {
                for (int y = startPoint.yCoordinate; y < startPoint.yCoordinate + width; y++) {
                    if (((x == startPoint.xCoordinate)
                            || (x == startPoint.xCoordinate + length - 1)
                            || (y == startPoint.yCoordinate)
                            || (y == startPoint.yCoordinate + width - 1))
                            && (world[x][y] != Tileset.FLOOR)) {
                        Random rand = new Random(seed + randomOffset);
                        randomOffset++;
                        boolean cornerChecker = true;
                        boolean selectedPosition = RandomUtils.bernoulli(rand, P);

                        if ((x == startPoint.xCoordinate && y == startPoint.yCoordinate)
                                || (x == startPoint.xCoordinate && y == startPoint.yCoordinate + width - 1)
                                || (x == startPoint.xCoordinate + length - 1 && y == startPoint.yCoordinate)
                                || (x == startPoint.xCoordinate + length - 1
                                && y == startPoint.yCoordinate + width - 1)) {
                            cornerChecker = false;
                        }
                        if ((x - 1 < 0) || (x + 1 > WIDTH) || (y - 1 < 0) || (y + 1 > HEIGHT)) {
                            cornerChecker = false;
                        }
                        if (new Position(x, y).isFixed()) {
                            cornerChecker = false;
                        }
                        if (selectedPosition && cornerChecker) {
                            int xDirection = 0;
                            int yDirection = 0;
                            if (y == startPoint.yCoordinate) {
                                yDirection = -1;
                            } else if (y == startPoint.yCoordinate + width - 1) {
                                yDirection = 1;
                            } else if (x == startPoint.xCoordinate) {
                                xDirection = -1;
                            } else {
                                xDirection = 1;
                            }
                            tree.doors[tree.doorsPtr] = new Position(x, y, xDirection, yDirection);
                            tree.doorsPtr++;
                            if (tree.doorsPtr >= tree.doors.length) {
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void createDoor(Tree tree, Tree ref, TETile[][] world) {
        Position door = tree.doors[tree.doorsPtr - 1];
        world[door.xCoordinate][door.yCoordinate] = Tileset.FLOOR;

        int bound1, bound2;
        if (door.xDirection != 0) {
            bound1 = max(tree.pos.yCoordinate, ref.pos.yCoordinate);
            bound2 = Math.min(tree.pos.yCoordinate + tree.width - 1, ref.pos.yCoordinate + ref.width - 1);
            for (int y = bound1; y <= bound2; y++) {
                isFixedArray[door.xCoordinate][y] = 1;
            }
        } else {
            bound1 = max(tree.pos.xCoordinate, ref.pos.xCoordinate);
            bound2 = Math.min(tree.pos.xCoordinate + tree.length - 1, ref.pos.xCoordinate + ref.length - 1);
            for (int x = bound1; x <= bound2; x++) {
                isFixedArray[x][door.yCoordinate] = 1;
            }
        }
    }

    /* Create entrances on specified positions. And fix the walls. */
    private void breakWall(Tree parent, Tree child, TETile[][] world) {
        createDoor(parent, child, world);
        createDoor(child, parent, world);
    }

    private int shorterBoarder() {
        if (WIDTH < HEIGHT) {
            return WIDTH - 1;
        } else {
            return HEIGHT - 1;
        }
    }

    private Position startPointGenerator() {
        int widthLimit = HEIGHT / 10;
        int lengthLimit = WIDTH / 10;
        Random rand = new Random(seed + randomOffset);
        randomOffset++;
        int width = RandomUtils.uniform(rand, widthLimit);
        int length = RandomUtils.uniform(rand, lengthLimit);
        return new Position(length, width);
    }

    /* Direction, true: vertical, false: horizontal. */
    private Position startPointGenerator(Position door, int sideLength, int refLength, Random rand) {
        int xStart, yStart;
        if (door.xDirection != 0) {
            if (door.yCoordinate == 0) {
                yStart = 0;
            } else {
                yStart = RandomUtils.uniform(rand,
                        max(door.yCoordinate - sideLength + 2, 0),
                        Math.min(door.yCoordinate, HEIGHT));
            }
            if (door.xDirection == 1) {
                xStart = door.xCoordinate;
            } else {
                xStart = max(door.xCoordinate - refLength + 1, 0);
            }
        } else {
            if (door.xCoordinate == 0) {
                xStart = 0;
            } else {
                xStart = RandomUtils.uniform(rand,
                        max(door.xCoordinate - sideLength + 2, 0),
                        Math.min(door.xCoordinate, WIDTH));
            }
            if (door.yDirection == 1) {
                yStart = door.yCoordinate;
            } else {
                yStart = max(door.yCoordinate - refLength + 1, 0);
            }
        }
        return new Position(xStart, yStart);
    }

    /* true: room / vertical, false: hallway / horizontal. */
    private boolean typeGenerator(double threshold) {
        Random rand = new Random(seed + randomOffset);
        randomOffset++;
        double p = RandomUtils.uniform(rand);
        return p < threshold;
    }

    public Tree oneBuildingGenerator(TETile[][] world, Position door) {
        boolean isRoom = typeGenerator(0.5);
        boolean isVertical;
        Tree tree = null;

        int budget = 10;
        while (budget > 0) {
            Random rand = new Random(seed + randomOffset);
            randomOffset++;
            int length, width;
            if (!isRoom) {
                isVertical = typeGenerator(0.5);
                int HALLWAY_WIDTH = 3;
                if (isVertical) {
                    length = HALLWAY_WIDTH;
                    width = RandomUtils.uniform(rand, HALLWAY_WIDTH, HALLWAY_LENGTH_UPPER);
                } else {
                    width = HALLWAY_WIDTH;
                    length = RandomUtils.uniform(rand, HALLWAY_WIDTH, HALLWAY_LENGTH_UPPER);
                }
            } else {
                int ROOM_LENGTH_UPPER = WIDTH / 8;
                int ROOM_LOWER = 4;
                length = RandomUtils.uniform(rand, ROOM_LOWER, ROOM_LENGTH_UPPER);
                int ROOM_WIDTH_UPPER = HEIGHT / 5;
                width = RandomUtils.uniform(rand, ROOM_LOWER, ROOM_WIDTH_UPPER);
            }

            Position startPoint;
            if (door.xDirection != 0) {
                startPoint = startPointGenerator(door, width, length, rand);
            } else {
                startPoint = startPointGenerator(door, length, width, rand);
            }

            boolean blank = RectangleGenerator.blankArea(length, width, startPoint);
            tree = new Tree(startPoint);
            tree.isRoom = isRoom;
            if (blank) {
                RectangleGenerator.generator(length, width, startPoint, world);
                tree.length = length;
                tree.width = width;
                tree.fail = false;
                break;
            }
            budget--;
        }
        return tree;
    }

    public void mazeGenerator(TETile[][] world) {
        Position startPosition = startPointGenerator();
        Tree parent = oneBuildingGenerator(world, startPosition);
        Tree child = null;
        parent.doorsPtr++;
        int trial = 0;

        while (trial < 200) {
            int count = 0;
            while (count < 100) {
                selectDoor(parent, world);
                Position doorPosition = parent.doors[parent.doors.length - 1];
                Position newPos = new Position(doorPosition.xCoordinate + doorPosition.xDirection,
                        doorPosition.yCoordinate + doorPosition.yDirection, doorPosition.xDirection,
                        doorPosition.yDirection);
                child = oneBuildingGenerator(world, newPos);
                if (!child.fail) {
                    child.doors[child.doorsPtr] = newPos;
                    child.doors[child.doorsPtr].xDirection = doorPosition.xDirection;
                    child.doors[child.doorsPtr].yDirection = doorPosition.yDirection;
                    child.doorsPtr++;
                    breakWall(parent, child, world);
                    break;
                } else {
                    parent.doorsPtr--;
                    count++;
                }
            }
            if (child.fail) {
                break;
            }
            child.parent = parent;
            parent.child = child;
            parent = child;
            trial++;
        }
    }
}
