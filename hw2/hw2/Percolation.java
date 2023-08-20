package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import static org.junit.Assert.*;

public class Percolation {
    private final WeightedQuickUnionUF gridUnion;
    private final WeightedQuickUnionUF gridBackup;
    private final boolean[] openArr;
    private final int arr;
    private int numOfOpen;

    // Convert a coordinate into an integer.
    private int coordinate2Int(int row, int col) {
        return arr * row + col + 1;
    }

    // Validate the input value
    private void validate(int row, int col) {
        if (row < 0 || row >= arr || col < 0 || col >= arr) {
            throw new IndexOutOfBoundsException("The index is out of the grid.");
        }
    }

    private boolean boundChecker(int row, int col) {
        return row >= 0 && row <= arr - 1 && col >= 0 && col <= arr - 1;
    }

    // Create N-by-N grid, with all sites initially blocked
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be greater than 0.");
        }
        gridUnion = new WeightedQuickUnionUF(N * N + 2);
        gridBackup  = new WeightedQuickUnionUF(N * N + 1);
        arr = N;
        numOfOpen = 0;
        openArr = new boolean[N * N + 2];
        for (int col = 0; col < N; col++) {
            gridUnion.union(0, coordinate2Int(0, col));
            gridUnion.union(N * N + 1, coordinate2Int(N - 1, col));
            gridBackup.union(0, coordinate2Int(0, col));
        }
    }

    // Check the situation of a neighbor.
    private void neighborCheck(int row, int col, int ref) {
        int index = coordinate2Int(row, col);
        if (boundChecker(row, col) && isOpen(row, col)) {
            gridUnion.union(index, ref);
            gridBackup.union(index, ref);
        }
    }

    // Open the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);
        if (!isOpen(row, col)) {
            int num = coordinate2Int(row, col);
            openArr[num] = true;
            numOfOpen++;
            neighborCheck(row - 1, col, num);
            neighborCheck(row + 1, col, num);
            neighborCheck(row, col - 1, num);
            neighborCheck(row, col + 1, num);
        }
    }

    // Is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        int num = coordinate2Int(row, col);
        return openArr[num];
    }

    // Is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        int num = coordinate2Int(row, col);
        return isOpen(row, col) && gridBackup.connected(0, num);
    }

    // Number of open sites.
    public int numberOfOpenSites() {
        return numOfOpen;
    }

    // Does the system percolate?
    public boolean percolates() {
        return gridUnion.connected(0, arr * arr + 1);
    }

    // Use for unit testing (not required)
    public static void main(String[] args) {
        Percolation grid = new Percolation(5);
        grid.open(3, 4);
        grid.open(2, 4);
        grid.open(2, 2);
        grid.open(2, 3);
        assertTrue(grid.isOpen(2, 3));

        grid.open(0, 2);
        assertTrue(grid.isFull(0, 2));
        grid.open(1, 2);
        assertTrue(grid.isFull(1, 2));
        assertEquals(6, grid.numberOfOpenSites());
        assertTrue(grid.isFull(3, 4));
        grid.open(4, 4);
        assertTrue(grid.percolates());
        grid.open(4, 2);
        assertFalse(grid.isFull(4, 2));
    }
}
