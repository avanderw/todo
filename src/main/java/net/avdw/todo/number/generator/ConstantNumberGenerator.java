package net.avdw.todo.number.generator;

public class ConstantNumberGenerator implements NumberGenerator {
    private final Double number;

    ConstantNumberGenerator(final Double number) {
        this.number = number;
    }

    /**
     * Generate a value.
     *
     * @return a value following the implementation rule
     */
    @Override
    public Double nextValue() {
        return number;
    }
}
