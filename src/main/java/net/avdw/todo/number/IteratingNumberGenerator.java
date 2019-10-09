package net.avdw.todo.number;

import com.google.inject.Inject;

import java.util.List;

public class IteratingNumberGenerator {
    private int currentIdx = -1;
    private int direction = 1;
    private final boolean wrap;
    private final List<Double> numbers;

    @Inject
    public IteratingNumberGenerator(List<Double> numbers, boolean wrap) {
        this.numbers = numbers;
        this.wrap = wrap;
    }

    public double nextValue() {
        currentIdx += direction;

        if (currentIdx < 0 || currentIdx == numbers.size()) {
            if (wrap) {
                currentIdx %= numbers.size();
            } else {
                direction *= -1;
                currentIdx += 2 * direction;
            }
        }

        return numbers.get(currentIdx);
    }

    public static void main(String[] args) {
        NumberInterpolater numberInterpolater = new NumberInterpolater(Interpolation.BACK_EASE_IN_OUT);
        NumberSampler sampler = new NumberSampler(numberInterpolater);
        IteratingNumberGenerator instance = new IteratingNumberGenerator(sampler.sample(3, 30, 10), false);
        for (int i = 0; i < 24; i++) {
            System.out.print(String.format(" %4.2f ", instance.nextValue()));
        }
        System.out.println();
    }
}
