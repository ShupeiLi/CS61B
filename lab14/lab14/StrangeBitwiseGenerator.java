package lab14;

import lab14lib.Generator;

public class StrangeBitwiseGenerator implements Generator {
    private int period;
    private int state;

    public StrangeBitwiseGenerator(int period) {
        this.period = period;
    }

    public double next() {
//      int weirdState = state & (state >>> 3) % period;
//      int weirdState = state & (state >> 3) & (state >> 8) % period;
        int weirdState = state & (state >> 7) % period;
        double value = (2 / (double) period) * weirdState - 1;
        state++;
        return value;
    }
}
