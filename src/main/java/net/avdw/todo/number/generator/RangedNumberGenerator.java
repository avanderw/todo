package net.avdw.todo.number.generator;

public class RangedNumberGenerator implements NumberGenerator {
    private final Double min;
    private final Double max;
    private RandomGenerator randomGenerator;

    RangedNumberGenerator(Double min, Double max, RandomGenerator randomGenerator) {
        this.min = min;
        this.max = max;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Double nextValue() {
        return randomGenerator.nextBetween(min, max);
    }
}
