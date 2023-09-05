import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        // Initialization.
        for (GraphDB.Node p : g.nodes.values()) {
            p.distTo = Double.MAX_VALUE;
            p.estimateDist = Double.MAX_VALUE;
        }
        GraphDB.Node source = g.nodes.get(g.closest(stlon, stlat));
        GraphDB.Node destination = g.nodes.get(g.closest(destlon, destlat));
        source.distTo = 0;
        source.estimateDist = 0;
        PriorityQueue<GraphDB.Node> fringe = new PriorityQueue<>(new NodeComparator());
        Set<GraphDB.Node> marked = new HashSet<>();
        Map<Long, Long> edgeTo = new HashMap<>();
        GraphDB.Node v = source;

        while (!v.equals(destination)) {
            if (!marked.contains(v)) {
                marked.add(source);
                for (GraphDB.Node w : v.neighbors) {
                    w.estimateDist = g.distance(destination.id, w.id);
                    double dist = v.distTo + g.distance(v.id, w.id);
                    if (dist <= w.distTo) {
                        w.distTo = dist;
                        if (edgeTo.containsKey(w.id)) {
                            edgeTo.replace(w.id, v.id);
                        } else {
                            edgeTo.put(w.id, v.id);
                        }
                        fringe.add(w);
                    }
                }
            }
            marked.add(v);
            v = fringe.poll();
            if (v == null) {
                break;
            }
        }

        List<Long> path = new LinkedList<>();
        long vId = destination.id;
        while (vId != source.id) {
            path.add(0, vId);
            vId = edgeTo.get(vId);
        }
        path.add(0, source.id);
        return path;
    }

    private static class NodeComparator implements Comparator<GraphDB.Node> {
        @Override
        public int compare(GraphDB.Node node1, GraphDB.Node node2) {
            double node1Dist = node1.distTo + node1.estimateDist;
            double node2Dist = node2.distTo + node2.estimateDist;
            double diff = node1Dist - node2Dist;
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigationDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<NavigationDirection> navigationList = new LinkedList<>();
        GraphDB.Node ptr = g.nodes.get(route.get(0));
        GraphDB.Edge edge = ptr.edgeRef.getFirst();

        for (int i = 1; i < route.size() - 1; i++) {
            GraphDB.Node ptrNext = g.nodes.get(route.get(i));
            GraphDB.Edge edgeNext = ptrNext.edgeRef.getFirst();
            if (i == 1 || !edgeNext.equals(edge)) {
                NavigationDirection direction = new NavigationDirection();
                edge = edgeNext;
                if (edge.name != null) {
                    direction.way = edge.name;
                }
                direction.distance = g.distance(ptr.id, ptrNext.id);
                if (i == 1) {
                    direction.direction = NavigationDirection.START;
                } else {
                    double angle = g.bearing(route.get(i - 1), ptrNext.id);
                    if (angle < -100) {
                        direction.direction = NavigationDirection.SHARP_LEFT;
                    } else if (angle >= -100 && angle < -30) {
                        direction.direction = NavigationDirection.LEFT;
                    } else if (angle >= -30 && angle < -15) {
                        direction.direction = NavigationDirection.SLIGHT_LEFT;
                    } else if (angle >= -15 && angle < 15) {
                        direction.direction = NavigationDirection.STRAIGHT;
                    } else if (angle >= 15 && angle < 30) {
                        direction.direction = NavigationDirection.SLIGHT_RIGHT;
                    } else if (angle >= 30 && angle < 100) {
                        direction.direction = NavigationDirection.RIGHT;
                    } else {
                        direction.direction = NavigationDirection.SHARP_RIGHT;
                    }
                }
                navigationList.add(direction);
                ptr = ptrNext;
            }
        }
        return navigationList;
    }

    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }
}
