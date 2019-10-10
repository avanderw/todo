package net.avdw.todo.number.generator;

import java.util.Random;

public class RandomGenerator implements NumberGenerator {
    private Random random;

    RandomGenerator(Random random) {
        this.random = random;
    }

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
    public int nextSign(double chance) {
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
    public boolean nextBoolean(double chance) {
        return random.nextDouble() < chance;
    }

    public Double nextBetween(Double min, Double max) {
        return min + (max - min) * random.nextDouble();
    }

    public Double nextOffset(Double baseNumber, Double minDeviation, Double maxDeviation, boolean inBothDirections) {
        return inBothDirections
                ? baseNumber + nextSign() * nextBetween(minDeviation, maxDeviation)
                : baseNumber + nextBetween(minDeviation, maxDeviation);
    }
}
