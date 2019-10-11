package net.avdw.todo.number.generator;

public class OffsetNumberGenerator implements NumberGenerator {
    private final Double baseNumber;
    private final Double minDeviation;
    private final Double maxDeviation;
    private final boolean inBothDirections;
    private final RandomGenerator randomGenerator;

    OffsetNumberGenerator(final Double baseNumber, final Double minDeviation, final Double maxDeviation, final boolean inBothDirections, final RandomGenerator randomGenerator) {
        this.baseNumber = baseNumber;
        this.minDeviation = minDeviation;
        this.maxDeviation = maxDeviation;
        this.inBothDirections = inBothDirections;
        this.randomGenerator = randomGenerator;
    }

    /**
     * Generate a value.
     *
     * @return a value following the implementation rule
     */
    @Override
    public Double nextValue() {
        return randomGenerator.nextOffset(baseNumber, minDeviation, maxDeviation, inBothDirections);
    }
}
