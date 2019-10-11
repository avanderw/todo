package net.avdw.todo.number.generator;

import java.util.Random;

public class RandomGenerator implements NumberGenerator {
    private final Random random;

    RandomGenerator(final Random random) {
        this.random = random;
    }

    /**
     * Generate a value.
     *
     * @return a value following the implementation rule
     */
    @Override
    public Double nextValue() {
        return random.nextDouble();
    }

    /**
     * Generates -1 or 1 with 50% chance of 1.
     *
     * @return randomly 1 or -1
     */
    public int nextSign() {
        return nextSign(.5);
    }

    /**
     * Generates -1 or 1 with chance% of 1.
     *
     * @param chance chance to generate a 1
     * @return randomly 1 or -1
     */
    public int nextSign(final double chance) {
        return random.nextDouble() < chance ? 1 : -1;
    }

    /**
     * Generates true or false 50% of the time.
     *
     * @return randomly true or false
     */
    public boolean nextBoolean() {
        return nextBoolean(.5);
    }

    /**
     * Generates true or false chance% of the time.
     *
     * @param chance chance of getting true
     * @return randomly true or false
     */
    public boolean nextBoolean(final double chance) {
        return random.nextDouble() < chance;
    }

    /**
     * Generate a random value between [min..max).
     *
     * @param min min value
     * @param max max value
     * @return random number between min and max
     */
    public Double nextBetween(final Double min, final Double max) {
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Offset a number by a random deviation.
     *
     * @param baseNumber       the number to offset
     * @param minDeviation     the minimum deviation
     * @param maxDeviation     the maximum deviation
     * @param inBothDirections whether to randomly generate in the negative
     * @return a number offset by a random deviation
     */
    public Double nextOffset(final Double baseNumber, final Double minDeviation, final Double maxDeviation, final boolean inBothDirections) {
        return inBothDirections
                ? baseNumber + nextSign() * nextBetween(minDeviation, maxDeviation)
                : baseNumber + nextBetween(minDeviation, maxDeviation);
    }
}
