package lab14;

import lab14lib.Generator;

public class SawToothGenerator implements Generator {
    private int period;
    private int state;

    public SawToothGenerator(int period) {
        this.period = period;
    }

    public double next() {
        double value = (2 / (double) period) * (state % period) - 1;
        state++;
        return value;
    }
}
