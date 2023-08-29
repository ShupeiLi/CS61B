package lab11.graphs;

import edu.princeton.cs.algs4.Stack;

/**
 *  @author Josh Hug
 */
public class MazeCycles extends MazeExplorer {
    /* Inherits public fields:
    public int[] distTo;
    public int[] edgeTo;
    public boolean[] marked;
    */

    private Maze maze;
    private Stack<Integer> stack = new Stack<>();
    private final int START = 0;
    private boolean stopSignal = false;

    public MazeCycles(Maze m) {
        super(m);
        maze = m;
    }

    private void dfs(int v) {
        marked[v] = true;
        int parent;
        if (stack.isEmpty()) {
            parent = -1;
        } else {
            parent = stack.peek();
        }
        stack.push(v);
        announce();

        for (int w : maze.adj(v)) {
            if (marked[w] && w != parent) {
                int point = w;
                int prev;
                edgeTo[point] = v;
                while (!stack.isEmpty()) {
                    prev = stack.pop();
                    edgeTo[prev] = point;
                    point = prev;
                    if (point == w) {
                        break;
                    }
                }
                announce();
                stopSignal = true;
                return;
            }
        }

        for (int w : maze.adj(v)) {
            if (stopSignal) {
                return;
            }
            if (!marked[w]) {
                dfs(w);
            }
        }
    }

    @Override
    public void solve() {
        dfs(START);
    }
}

