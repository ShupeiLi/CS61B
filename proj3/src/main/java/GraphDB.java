import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */
    static class Node {
        long id;
        double lat, lon;
        String name;
        LinkedList<Node> neighbors = new LinkedList<>();
        LinkedList<Edge> edgeRef = new LinkedList<>();
        double distTo = Double.MAX_VALUE;
        double estimateDist = Double.MAX_VALUE;

        Node(long id, double lon, double lat) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
        }

        void setName(String name) {
            this.name = name;
        }
    }

    static class Edge {
        long id;
        LinkedList<Node> nodeRef = new LinkedList<>();
        String maxSpeed;
        String name;

        Edge(long id) {
            this.id = id;
        }

        void setMaxSpeed(String maxSpeed) {
            this.maxSpeed = maxSpeed;
        }

        void setName(String name) {
            this.name = name;
        }

        void addRef() {
            Node ptr = nodeRef.get(0);
            ptr.edgeRef.add(this);
            for (int i = 1; i < nodeRef.size(); i++) {
                Node ptrNext = nodeRef.get(i);
                ptrNext.edgeRef.add(this);
                ptr.neighbors.add(ptrNext);
                ptrNext.neighbors.add(ptr);
                ptr = ptrNext;
            }
        }
    }

    /** Node of a trie used for autocompletion and search. */
    static class TrieNode {
        boolean exists;
        List<Node> linkedNodes;
        Map<String, TrieNode> nodeList;

        public TrieNode() {
            exists = false;
            nodeList = new HashMap<>();
            linkedNodes = new LinkedList<>();
        }
    }

    Map<Long, Node> nodes = new HashMap<>();
    Map<Long, Edge> edges = new HashMap<>();

    /** The root of the trie. */
    static TrieNode root = new TrieNode();

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            FileInputStream inputStream = new FileInputStream(inputFile);
            // GZIPInputStream stream = new GZIPInputStream(inputStream);

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputStream, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Queue<Node> isolatedNodes = new LinkedList<>();
        for (Node p : nodes.values()) {
            if (p.neighbors.isEmpty()) {
                isolatedNodes.add(p);
            }
        }
        for (Node p : isolatedNodes) {
            nodes.remove(p.id, p);
        }
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of id's of all vertices in the graph.
     */
    Iterable<Long> vertices() {
        return nodes.keySet();
    }

    /**
     * Returns ids of all vertices adjacent to v.
     * @param v The id of the vertex we are looking adjacent to.
     * @return An iterable of the ids of the neighbors of v.
     */
    Iterable<Long> adjacent(long v) {
        LinkedList<Long> indexList = new LinkedList<>();
        for (Node p : nodes.get(v).neighbors) {
            indexList.add(p.id);
        }
        return indexList;
    }

    /**
     * Returns the great-circle distance between vertices v and w in miles.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The great-circle distance between the two locations from the graph.
     */
    double distance(long v, long w) {
        return distance(lon(v), lat(v), lon(w), lat(w));
    }

    static double distance(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double dphi = Math.toRadians(latW - latV);
        double dlambda = Math.toRadians(lonW - lonV);

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 3963 * c;
    }

    /**
     * Returns the initial bearing (angle) between vertices v and w in degrees.
     * The initial bearing is the angle that, if followed in a straight line
     * along a great-circle arc from the starting point, would take you to the
     * end point.
     * Assumes the lon/lat methods are implemented properly.
     * <a href="https://www.movable-type.co.uk/scripts/latlong.html">Source</a>.
     * @param v The id of the first vertex.
     * @param w The id of the second vertex.
     * @return The initial bearing between the vertices.
     */
    double bearing(long v, long w) {
        return bearing(lon(v), lat(v), lon(w), lat(w));
    }

    static double bearing(double lonV, double latV, double lonW, double latW) {
        double phi1 = Math.toRadians(latV);
        double phi2 = Math.toRadians(latW);
        double lambda1 = Math.toRadians(lonV);
        double lambda2 = Math.toRadians(lonW);

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    /**
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    long closest(double lon, double lat) {
        double minDistance = Double.MAX_VALUE;
        long minId = 0;
        for (Node p : nodes.values()) {
            double dist = distance(p.lon, p.lat, lon, lat);
            if (dist < minDistance) {
                minDistance = dist;
                minId = p.id;
            }
        }
        return minId;
    }

    /**
     * Gets the longitude of a vertex.
     * @param v The id of the vertex.
     * @return The longitude of the vertex.
     */
    double lon(long v) {
        return nodes.get(v).lon;
    }

    /**
     * Gets the latitude of a vertex.
     * @param v The id of the vertex.
     * @return The latitude of the vertex.
     */
    double lat(long v) {
        return nodes.get(v).lat;
    }

    void putTrie(String key, Node node) {
        putTrie(root, key, 0, node);
    }

    private TrieNode putTrie(TrieNode x, String key, int d, Node node) {
        if (x == null) {
            x = new TrieNode();
        }
        if (d == key.length()) {
            x.exists = true;
            x.linkedNodes.add(node);
            return x;
        }
        String s = key.substring(d, d + 1);
        x.nodeList.put(s, putTrie(x.nodeList.get(s), key, d + 1, node));
        return x;
    }

    public static List<String> getLocationsByPrefix(String prefix) {
        List<String> stringList = new ArrayList<>();
        return GraphDB.searchTrie(stringList, prefix, false);
    }

    public static List<Map<String, Object>> getLocations(String locationName) {
        List<Map<String, Object>> locationMap = new ArrayList<>();
        GraphDB.searchTrie(locationMap, locationName, true);
        List<Map<String, Object>> newMap = new ArrayList<>();
        for (Map<String, Object> place : locationMap) {
            if (cleanString((String) place.get("name")).equals(cleanString(locationName))) {
                newMap.add(place);
            }
        }
        return newMap;
    }

    static <K> List<K> searchTrie(List<K> kList, String key, boolean nodeLink) {
        String currentPtr = "";
        searchTrie(cleanString(key), kList, currentPtr, root, nodeLink, 0);
        return kList;
    }

    private static <K> void searchTrie(String key, List<K> kList, String currentPtr, TrieNode x,
                                       boolean nodeLink, int indexSub) {
        if (cleanString(currentPtr).equals(key)) {
            dfsTrie(kList, currentPtr, x, nodeLink);
        } else if (x.nodeList.isEmpty()) {
            return;
        } else {
            String subKey = key.substring(indexSub, indexSub + 1);
            for (String k : x.nodeList.keySet()) {
                String kClean = cleanString(k);
                if (kClean.equals(subKey)) {
                    searchTrie(key, kList, currentPtr.concat(k), x.nodeList.get(k), nodeLink, indexSub + 1);
                }
                if (kClean.isEmpty()) {
                    searchTrie(key, kList, currentPtr.concat(k), x.nodeList.get(k), nodeLink, indexSub);
                }
            }
        }
    }

    private static <K> void dfsTrie(List<K> kList, String currentPtr, TrieNode x, boolean nodeLink) {
        if (x.exists) {
            if (!nodeLink) {
                kList.add((K) currentPtr);
            } else {
                for (Node p : x.linkedNodes) {
                    Map<String, Object> nodeMap = new HashMap<>();
                    nodeMap.put("lat", p.lat);
                    nodeMap.put("lon", p.lon);
                    nodeMap.put("name", p.name);
                    nodeMap.put("id", p.id);
                    kList.add((K) nodeMap);
                }
            }
        }
        if (x.nodeList.isEmpty()) {
            return;
        } else {
            for (String k : x.nodeList.keySet()) {
                dfsTrie(kList, currentPtr.concat(k), x.nodeList.get(k), nodeLink);
            }
        }
    }
}
