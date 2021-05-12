package net.avdw.todo.number;

import javax.inject.Inject;
import java.util.function.Function;

public class NumberInterpolater {
    private final Function<Double, Double> interpolationFunction;


    public NumberInterpolater() {
        this(Interpolation.LINEAR);
    }

    /**
     * @param interpolationFunction the interpolation function to apply
     */
    @Inject
    public NumberInterpolater(final Function<Double, Double> interpolationFunction) {
        this.interpolationFunction = (interpolationFunction == null)
                ? Interpolation.LINEAR
                : interpolationFunction;
    }

    /**
     * Interpolate a number between two numbers given a weight and interpolation function.
     *
     * @param from   the number to interpolate from
     * @param to     the number to interpolate to
     * @param weight the weight to apply in the range [0..1]
     * @return the interpolated value
     */
    public double interpolate(final double from, final double to, final double weight) {
        final double distance = Math.abs(from - to);

        return from < to
                ? Math.min(from, to) + distance * interpolationFunction.apply(weight)
                : Math.max(from, to) - distance * interpolationFunction.apply(weight);
    }
}
