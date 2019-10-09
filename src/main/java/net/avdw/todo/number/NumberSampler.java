package net.avdw.todo.number;

import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public class NumberSampler {
    private NumberNormaliser numberNormaliser;
    private NumberInterpolater numberInterpolater;

    NumberSampler() {
        this(new NumberNormaliser(), new NumberInterpolater());
    }

    NumberSampler(NumberInterpolater numberInterpolater) {
        this(new NumberNormaliser(), numberInterpolater);
    }

    /**
     * @param numberNormaliser   the way in which to normalise the sample iteration
     * @param numberInterpolater the way in which to interpolate the numbers
     */
    @Inject
    public NumberSampler(NumberNormaliser numberNormaliser, NumberInterpolater numberInterpolater) {
        this.numberNormaliser = numberNormaliser;
        this.numberInterpolater = numberInterpolater;
    }

    /**
     * Sample a list of numbers from a range. Apply an interpolation function to the sampling.
     *
     * @param start       the start of the range
     * @param end         the end of the range
     * @param sampleCount the amount of samples to collect
     * @return a list of numbers samples from the range
     */
    public List<Double> sample(double start, double end, int sampleCount) {
        List<Double> sampleList = new ArrayList<>();
        for (int i = 0; i < sampleCount; i++) {
            double weight = numberNormaliser.normalise(i, sampleCount - 1);
            sampleList.add(numberInterpolater.interpolate(start, end, weight));
        }

        return sampleList;
    }
}
