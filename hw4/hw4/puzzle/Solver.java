package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;

import java.util.Arrays;
import java.util.Comparator;

public class Solver {
    /** Search node. */
    private class SearchNode {
        private final WorldState state;
        private final int distTo;
        private final SearchNode prev;
        private final int estimateDist;

        public SearchNode(WorldState state, int distTo, SearchNode prev) {
            this.state = state;
            this.distTo = distTo;
            this.prev = prev;
            this.estimateDist = state.estimatedDistanceToGoal();
        }
    }

    /** Compare two search nodes. */
    private class SearchNodeComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode node1, SearchNode node2) {
            int node1Dist = node1.distTo + node1.estimateDist;
            int node2Dist = node2.distTo + node2.estimateDist;
            return node1Dist - node2Dist;
        }
    }

    private SearchNode finalNode;

    /** Solver(initial): Constructor which solves the puzzle, computing
     everything necessary for moves() and solution() to
     not have to solve the problem again. Solves the
     puzzle using the A* algorithm. Assumes a solution exists. */
    public Solver(WorldState initial) {
        MinPQ<SearchNode> pq = new MinPQ<>(new SearchNodeComparator());
        SearchNode init = new SearchNode(initial, 0, null);
        pq.insert(init);
        while (!pq.isEmpty()) {
            SearchNode x = pq.delMin();
            if (x.state.isGoal()) {
                finalNode = x;
                break;
            }
            for (WorldState state : x.state.neighbors()) {
                if ((x.state.equals(initial)) || (!state.equals(x.prev.state))) {
                    pq.insert(new SearchNode(state, x.distTo + 1, x));
                }
            }
        }
    }

    /**  Returns the minimum number of moves to solve the puzzle starting
     at the initial WorldState. */
    public int moves() {
        return finalNode.distTo;
    }

    /** Returns a sequence of WorldStates from the initial WorldState
     to the solution. */
    public Iterable<WorldState> solution() {
        WorldState[] stateList = new WorldState[finalNode.distTo + 1];
        SearchNode node = finalNode;
        int ptr = finalNode.distTo;
        while (ptr >= 0) {
            stateList[ptr] = node.state;
            ptr--;
            node = node.prev;
        }
        return Arrays.asList(stateList);
    }
}
