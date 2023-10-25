import edu.princeton.cs.algs4.Picture;
import java.awt.*;


public class SeamCarver {
    private final Picture picture;
    private final double[][] energyArray;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        energyArray = new double[width()][height()];
        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[i].length; j++) {
                energyArray[i][j] = energy(i, j);
            }
        }
    }

    /** Current picture */
    public Picture picture() {
        return picture;
    }

    /** Width of current picture */
    public int width() {
        return picture.width();
    }

    /** Height of current picture */
    public int height() {
        return picture.height();
    }

    /** Check validity of x and y */
    private void checkBoundary(int x, int y) {
        if ((x < 0) || (x > width() - 1) || (y < 0) || (y > height() - 1)) {
            throw new java.lang.IndexOutOfBoundsException("Invalid width or height parameter(s).");
        }
    }

    /** Energy of pixel at column x and row y */
    public double energy(int x, int y) {
        checkBoundary(x, y);
        Color n, s, w, e;
        int width = width();
        int height = height();
        if (x - 1 < 0) {
            w = picture.get(width - 1, y);
        } else {
            w = picture.get(x - 1, y);
        }
        if (x + 1 >= width) {
            e = picture.get(0, y);
        } else {
            e = picture.get(x + 1, y);
        }
        if (y - 1 < 0) {
            n = picture.get(x, height - 1);
        } else {
            n = picture.get(x, y - 1);
        }
        if (y + 1 >= height) {
            s = picture.get(x, 0);
        } else {
            s = picture.get(x, y + 1);
        }
        return energyFunction(w, e) + energyFunction(n, s);
    }

    private double energyFunction(Color n1, Color n2) {
        double r = n1.getRed() - n2.getRed();
        double g = n1.getGreen() - n2.getGreen();
        double b = n1.getBlue() - n2.getBlue();
        return r * r + g * g + b * b;
    }

    /** Sequence of indices for horizontal seam */
    public int[] findHorizontalSeam() {
        return findHorizontalSeamHelper(width(), height(), energyArray);
    }

    /** Sequence of indices for vertical seam */
    public int[] findVerticalSeam() {
        double[][] resizedEnergy = new double[height()][width()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                resizedEnergy[j][i] = energyArray[i][j];
            }
        }
        return findHorizontalSeamHelper(height(), width(), resizedEnergy);
    }

    private int[] findHorizontalSeamHelper(int height, int width, double[][] energyArr) {
        double[] costs = new double[width];
        int[][] routes = new int[width][height];
        double[] routesCosts = new double[width];
        System.arraycopy(energyArr[0], 0, costs, 0, width);
        for (int i = 1; i < height + 1; i++) {
            double[] copy = new double[width];
            System.arraycopy(costs, 0, copy, 0, width);
            for (int j = 0; j < width; j++) {
                double minCost = Double.MAX_VALUE;
                int minIndex = 0;
                int lastStep = j;
                if (i >= 2) {
                    lastStep = routes[j][i - 2];
                }
                for (int k = Math.max(0, lastStep - 1); k < Math.min(width, lastStep + 2); k++) {
                    double current = copy[k];
                    if (current < minCost) {
                        minCost = current;
                        minIndex = k;
                    }
                }
                routes[j][i - 1] = minIndex;
                routesCosts[j] += energyArr[i - 1][minIndex];
                if (i < height) {
                    costs[j] = minCost + energyArr[i][j];
                }
            }
        }
        double minCost = Double.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < width; i++) {
            double current = routesCosts[i];
            if (current < minCost) {
                minCost = current;
                minIndex = i;
            }
        }
        return routes[minIndex];
    }

    private void checkSeam(int[] seam) {
        int previous = seam[0];
        for (int item : seam) {
            if (Math.abs(item - previous) > 1) {
                throw new IllegalArgumentException("The seam is invalid.");
            }
        }
    }

    /** Remove horizontal seam from picture */
    public void removeHorizontalSeam(int[] seam) {
        checkSeam(seam);
        SeamRemover.removeHorizontalSeam(picture, seam);
    }

    /** Remove vertical seam from picture */
    public void removeVerticalSeam(int[] seam) {
        checkSeam(seam);
        SeamRemover.removeVerticalSeam(picture, seam);
    }
}
