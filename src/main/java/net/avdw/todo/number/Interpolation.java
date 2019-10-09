package net.avdw.todo.number;

import java.util.function.Function;

/**
 * Collection of interpolation functions.
 * The values for p are in the range [0..1]
 */
public class Interpolation {
    public static final Function<Double, Double> BACK_EASE_IN = (p) -> p * p * (3 * p - 2);
    public static final Function<Double, Double> BACK_EASE_OUT = (p) -> 1 - BACK_EASE_IN.apply(1 - p);
    public static final Function<Double, Double> BACK_EASE_IN_OUT = (p) -> (p < .5)
            ? BACK_EASE_IN.apply(p * 2) / 2
            : BACK_EASE_IN.apply(p * -2 + 2) / -2 + 1;
    public static final Function<Double, Double> LINEAR = (p) -> p;
}
