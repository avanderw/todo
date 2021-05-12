package net.avdw.todo.number;

import java.util.function.Function;

/**
 * Collection of interpolation functions.
 * The values for p are in the range [0..1]
 */
public final class Interpolation {
    /**
     * BACK.
     */
    public static final Function<Double, Double> BACK_EASE_IN = (p) -> p * p * (3 * p - 2);
    public static final Function<Double, Double> BACK_EASE_OUT = (p) -> easeOut(BACK_EASE_IN, p);
    public static final Function<Double, Double> BACK_EASE_IN_OUT = (p) -> easeInOut(BACK_EASE_IN, p);
    /**
     * BOUNCE.
     */
    public static final Function<Double, Double> BOUNCE_EASE_IN = (p) -> {
        double bounce = 3;
        double pow2 = Math.pow(2, bounce);
        while (p < (pow2 - 1) / 11) {
            pow2 = Math.pow(2, --bounce);
        }
        return 1 / Math.pow(4, 3 - bounce) - 7.5625 * Math.pow((pow2 * 3 - 2) / 22 - p, 2);
    };
    public static final Function<Double, Double> BOUNCE_EASE_OUT = (p) -> easeOut(BOUNCE_EASE_IN, p);
    public static final Function<Double, Double> BOUNCE_EASE_IN_OUT = (p) -> easeInOut(BOUNCE_EASE_IN, p);
    /**
     * CIRC.
     */
    public static final Function<Double, Double> CIRC_EASE_IN = (p) -> 1 - Math.sqrt(1 - p * p);
    public static final Function<Double, Double> CIRC_EASE_OUT = (p) -> easeOut(CIRC_EASE_IN, p);
    public static final Function<Double, Double> CIRC_EASE_IN_OUT = (p) -> easeInOut(CIRC_EASE_IN, p);
    /**
     * CUBIC.
     */
    public static final Function<Double, Double> CUBIC_EASE_IN = (p) -> Math.pow(p, 3);
    public static final Function<Double, Double> CUBIC_EASE_OUT = (p) -> easeOut(CUBIC_EASE_IN, p);
    public static final Function<Double, Double> CUBIC_EASE_IN_OUT = (p) -> easeInOut(CUBIC_EASE_IN, p);
    /**
     * ELASTIC.
     */
    public static final Function<Double, Double> ELASTIC_EASE_IN = (p) -> p == 0 || p == 1
            ? p
            : -Math.pow(2, 8 * (p - 1)) * Math.sin(((p - 1) * 80 - 7.5) * Math.PI / 15);
    public static final Function<Double, Double> ELASTIC_EASE_OUT = (p) -> easeOut(ELASTIC_EASE_IN, p);
    public static final Function<Double, Double> ELASTIC_EASE_IN_OUT = (p) -> easeInOut(ELASTIC_EASE_IN, p);
    /**
     * EXPO.
     */
    public static final Function<Double, Double> EXPO_EASE_IN = (p) -> Math.pow(p, 6);
    public static final Function<Double, Double> EXPO_EASE_OUT = (p) -> easeOut(EXPO_EASE_IN, p);
    public static final Function<Double, Double> EXPO_EASE_IN_OUT = (p) -> easeInOut(EXPO_EASE_IN, p);
    /**
     * LINEAR.
     */
    public static final Function<Double, Double> LINEAR = (p) -> p;
    /**
     * QUAD.
     */
    public static final Function<Double, Double> QUAD_EASE_IN = (p) -> Math.pow(p, 2);
    public static final Function<Double, Double> QUAD_EASE_OUT = (p) -> easeOut(QUAD_EASE_IN, p);
    public static final Function<Double, Double> QUAD_EASE_IN_OUT = (p) -> easeInOut(QUAD_EASE_IN, p);
    /**
     * QAURT.
     */
    public static final Function<Double, Double> QAURT_EASE_IN = (p) -> Math.pow(p, 4);
    public static final Function<Double, Double> QAURT_EASE_OUT = (p) -> easeOut(QAURT_EASE_IN, p);
    public static final Function<Double, Double> QAURT_EASE_IN_OUT = (p) -> easeInOut(QAURT_EASE_IN, p);
    /**
     * QUINT.
     */
    public static final Function<Double, Double> QUINT_EASE_IN = (p) -> Math.pow(p, 5);
    public static final Function<Double, Double> QUINT_EASE_OUT = (p) -> easeOut(QUINT_EASE_IN, p);
    public static final Function<Double, Double> QUINT_EASE_IN_OUT = (p) -> easeInOut(QUINT_EASE_IN, p);
    /**
     * SINE.
     */
    public static final Function<Double, Double> SINE_EASE_IN = (p) -> 1 - Math.cos(p * Math.PI / 2);
    public static final Function<Double, Double> SINE_EASE_OUT = (p) -> easeOut(SINE_EASE_IN, p);
    public static final Function<Double, Double> SINE_EASE_IN_OUT = (p) -> easeInOut(SINE_EASE_IN, p);

    private Interpolation() {
    }

    private static double easeOut(final Function<Double, Double> interpolationFunction, final double p) {
        return 1 - interpolationFunction.apply(1 - p);
    }

    private static double easeInOut(final Function<Double, Double> interpolationFunction, final double p) {
        return p < .5
                ? interpolationFunction.apply(p * 2) / 2
                : interpolationFunction.apply(p * -2 + 2) / -2 + 1;
    }
}
