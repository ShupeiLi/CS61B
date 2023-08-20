package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final PercolationFactory pf;
    private final int N;
    private final int T;
    private double[] results;

    // Convert an integer into a coordinate
    private int[] int2Coordinate(int num) {
        int temp = num - 1;
        int row = temp / N;
        int col = temp % N;
        return new int[]{row, col};
    }

    // Conduct one experiment util the system percolates.
    private double oneExperiment() {
        Percolation grid = pf.make(N);
        int[] index = new int[N * N];
        for (int i = 1; i <= N * N; i++) {
            index[i - 1] = i;
        }
        int ptr = 0;
        while (!grid.percolates() && ptr < index.length) {
            StdRandom.shuffle(index, ptr, index.length);
            int[] coordinate = int2Coordinate(index[ptr]);
            ptr++;
            grid.open(coordinate[0], coordinate[1]);
        }
        return ((double) grid.numberOfOpenSites()) / (N * N);
    }

    // Perform T independent experiments on an N-by-N grid.
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0) {
            throw new IllegalArgumentException("N must be a positive integer.");
        }
        if (T <= 0) {
            throw new IllegalArgumentException("T must be a positive integer");
        }
        this.pf = pf;
        this.N = N;
        this.T = T;
        results = new double[T];
        for (int t = 0; t < T; t++) {
            results[t] = oneExperiment();
        }
    }

    // Sample mean of percolation threshold.
    public double mean() {
        return StdStats.mean(results);
    }

    // Sample standard deviation of percolation threshold.
    public double stddev() {
        return StdStats.stddev(results);
    }

    // Low endpoint of 95% confidence interval.
    public double confidenceLow() {
        return mean() - (1.96 * stddev()) / Math.sqrt(T);
    }

    // High endpoint of 95%.
    public double confidenceHigh() {
        return mean() + (1.96 * stddev()) / Math.sqrt(T);
    }
}
