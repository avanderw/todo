package net.avdw.todo.number;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SampledNumberGenerator {
    private int currentIdx = -1;
    private int direction = 1;
    private final boolean wrap;
    private final List<Double> sampledNumbers;

    @Inject
    public SampledNumberGenerator(List<Double> numbers, boolean wrap) {
        this.sampledNumbers = numbers;
        this.wrap = wrap;
    }

    public static void main(String[] args) {
        NumberSampler sampler = new NumberSampler(0, 20, 10);
        SampledNumberGenerator instance = new SampledNumberGenerator(sampler, true);
        for (int i = 0; i < 21; i++) {
            System.out.print(instance.nextValue());
        }
    }

    public double nextValue() {
        currentIdx += direction;

        if (currentIdx < 0 || currentIdx == sampledNumbers.size()) {
            if (wrap) {
                currentIdx %= sampledNumbers.size();
            } else {
                direction *= -1;
                currentIdx += direction;
            }
        }

        return sampledNumbers.get(currentIdx);
    }
}
