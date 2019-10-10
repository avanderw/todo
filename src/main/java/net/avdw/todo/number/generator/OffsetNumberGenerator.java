package net.avdw.todo.number.generator;

public class OffsetNumberGenerator implements NumberGenerator {
    private final Double baseNumber;
    private final Double minDeviation;
    private final Double maxDeviation;
    private boolean inBothDirections;
    private RandomGenerator randomGenerator;

    OffsetNumberGenerator(Double baseNumber, Double minDeviation, Double maxDeviation, boolean inBothDirections, RandomGenerator randomGenerator) {
        this.baseNumber = baseNumber;
        this.minDeviation = minDeviation;
        this.maxDeviation = maxDeviation;
        this.inBothDirections = inBothDirections;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Double nextValue() {
        return randomGenerator.nextOffset(baseNumber, minDeviation, maxDeviation, inBothDirections);
    }
}
