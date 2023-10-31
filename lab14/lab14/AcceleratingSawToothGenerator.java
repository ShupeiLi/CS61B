package lab14;

import lab14lib.Generator;

public class AcceleratingSawToothGenerator implements Generator {
    private int period;
    private double rate;
    private int state;
    int dur;

    public AcceleratingSawToothGenerator(int period, double rate) {
        this.period = period;
        this.rate = rate;
    }

    public double next() {
        if (dur == period) {
            period *= rate;
            dur = 0;
        }
        double value = (2 / (double) period) * (dur % period) - 1;
        dur++;
        state++;
        return value;
    }
}
