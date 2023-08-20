package hw2;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final PercolationFactory pf;
    private final int N;
    private final int T;
    private final double[] results;

    // Conduct one experiment util the system percolates.
    private double oneExperiment() {
        Percolation grid = pf.make(N);
        int row, col;
        while (!grid.percolates()) {
            row = StdRandom.uniform(N);
            col = StdRandom.uniform(N);
            if (!grid.isOpen(row, col)) {
                grid.open(row, col);
            }
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
