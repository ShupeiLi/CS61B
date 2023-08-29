package lab11.graphs;

import edu.princeton.cs.algs4.MinPQ;
import java.util.Comparator;

/**
 *  @author Josh Hug
 */
public class MazeAStarPath extends MazeExplorer {
    private int s;
    private int t;
    private int targetX;
    private int targetY;
    private boolean targetFound = false;
    private Maze maze;

    private class SearchNode {
        private final int index;
        private final int distTo;
        private final int heuristic;
        private final SearchNode prev;

        public SearchNode(int index, int distTo, int heuristic, SearchNode prev) {
            this.index = index;
            this.distTo = distTo;
            this.heuristic = heuristic;
            this.prev = prev;
        }
    }

    /** Compare two search nodes. */
    private class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode node1, SearchNode node2) {
            return node1.heuristic + node1.distTo - (node2.heuristic + node2.distTo);
        }
    }

    public MazeAStarPath(Maze m, int sourceX, int sourceY, int targetX, int targetY) {
        super(m);
        maze = m;
        this.targetX = targetX;
        this.targetY = targetY;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Estimate of the distance from v to the target. Manhattan distance. */
    private int h(int v) {
        int vX = maze.toX(v);
        int vY = maze.toY(v);
        return Math.abs(vX - targetX) + Math.abs(vY - targetY);
    }

    /** Finds vertex estimated to be closest to target. */
    private int findMinimumUnmarked() {
        return -1;
        /* You do not have to use this method. */
    }

    /** Performs an A star search from vertex s. */
    private void astar(int s) {
        MinPQ<SearchNode> pq = new MinPQ<>(new SearchNodeComparator());
        SearchNode init = new SearchNode(s, 0, h(s), null);
        pq.insert(init);
        while (!pq.isEmpty()) {
            SearchNode v = pq.delMin();
            marked[v.index] = true;
            distTo[v.index] = v.distTo;
            if (v.prev != null) {
                edgeTo[v.index] = v.prev.index;
            }
            announce();
            if (v.index == t) {
                break;
            }
            for (int w : maze.adj(v.index)) {
                if ((v == init) || (!(w == v.prev.index))) {
                    pq.insert(new SearchNode(w, v.distTo + 1, h(w), v));
                }
            }
        }
    }

    @Override
    public void solve() {
        astar(s);
    }
}
