package net.avdw.todo.number;

import java.util.function.Function;

public class DoubleInterpolater {
    /**
     * Interpolate a number between two numbers given a weight and interpolation function.
     *
     * @param from                  the number to interpolate from
     * @param to                    the number to interpolate to
     * @param weight                the weight to apply in the range [0..1]
     * @param interpolationFunction the interpolation function to apply
     * @return the interpolated value
     */
    public double interpolate(double from, double to, double weight, Function<Double, Double> interpolationFunction) {
        if (interpolationFunction == null) {
            interpolationFunction = Interpolation.LINEAR;
        }

        double distance = Math.abs(from - to);

        return (from < to)
                ? Math.min(from, to) + distance * interpolationFunction.apply(weight)
                : Math.max(from, to) - distance * interpolationFunction.apply(weight);
    }
}
