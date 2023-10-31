import edu.princeton.cs.algs4.Picture;
import java.awt.*;


public class SeamCarver {
    private final Picture picture;
    private final double[][] energyArray;

    public SeamCarver(Picture picture) {
        this.picture = picture;
        energyArray = new double[height()][width()];
        for (int i = 0; i < energyArray.length; i++) {
            for (int j = 0; j < energyArray[i].length; j++) {
                energyArray[i][j] = energy(j, i);
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
        double[][] resizedEnergy = new double[width()][height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                resizedEnergy[i][j] = energyArray[j][i];
            }
        }
        return findVerticalSeamHelper(width(), height(), resizedEnergy);
    }

    /** Sequence of indices for vertical seam */
    public int[] findVerticalSeam() {
        return findVerticalSeamHelper(height(), width(), energyArray);
    }

    private int[] findVerticalSeamHelper(int width, int height, double[][] energyArr) {
        double[][] costs = new double[width][height];
        System.arraycopy(energyArr[0], 0, costs[0], 0, height);
        for (int i = 1; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double left = (j == 0) ? Double.MAX_VALUE : costs[i - 1][j - 1];
                double right = (j == (height - 1)) ? Double.MAX_VALUE : costs[i - 1][j + 1];
                costs[i][j] = Math.min(Math.min(left, costs[i - 1][j]), right) + energyArr[i][j];
            }
        }

        int[] route = new int[width];
        for (int i = width - 1; i >= 0; i--) {
            double minCost = Double.MAX_VALUE;
            int minIndex = 0;
            if (i == width - 1) {
                for (int j = 0; j < height; j++) {
                    if (costs[i][j] < minCost) {
                        minCost = costs[i][j];
                        minIndex = j;
                    }
                }
            } else {
                int lastStep = route[i + 1];
                double left = (lastStep == 0) ? Double.MAX_VALUE : costs[i][lastStep - 1];
                double middle = costs[i][lastStep];
                double right = (lastStep == (height - 1)) ? Double.MAX_VALUE : costs[i][lastStep + 1];
                if (left > right) {
                    if (right > middle) {
                        minIndex = lastStep;
                    } else {
                        minIndex = lastStep + 1;
                    }
                } else {
                    if (left > middle) {
                        minIndex = lastStep;
                    } else {
                        minIndex = lastStep - 1;
                    }
                }
            }
            route[i] = minIndex;
        }
        return route;
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
