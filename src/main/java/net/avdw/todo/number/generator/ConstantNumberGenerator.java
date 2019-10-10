package net.avdw.todo.number.generator;

public class ConstantNumberGenerator implements NumberGenerator {
    private Double number;

    ConstantNumberGenerator(Double number) {
        this.number = number;
    }

    @Override
    public Double nextValue() {
        return number;
    }
}
