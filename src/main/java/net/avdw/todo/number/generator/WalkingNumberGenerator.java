package net.avdw.todo.number.generator;

public class WalkingNumberGenerator implements NumberGenerator {
    private Double walkingNumber;
    private final Double minWalk;
    private final Double maxWalk;
    private final boolean inBothDirections;
    private final RandomGenerator randomGenerator;

    WalkingNumberGenerator(Double walkingNumber, Double minWalk, Double maxWalk, boolean inBothDirections, RandomGenerator randomGenerator) {
        this.walkingNumber = walkingNumber;
        this.minWalk = minWalk;
        this.maxWalk = maxWalk;
        this.inBothDirections = inBothDirections;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Double nextValue() {
        return walkingNumber += randomGenerator.nextOffset(walkingNumber, minWalk, maxWalk, inBothDirections);
    }
}
