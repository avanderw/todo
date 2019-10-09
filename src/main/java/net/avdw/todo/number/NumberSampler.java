package net.avdw.todo.number;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public class DoubleSampler {
    public List<Double> sample(double start, double end, int sampleCount, Function<Double, Double> interpolationFunction) {
        List<Double> sampleList = new ArrayList<>();
        if (interpolationFunction == null) {
            interpolationFunction = Interpolation.LINEAR;
        }

        for (int i = 0; i < sampleCount; i++) {

            sampleList.add(interpolationFunction.apply());
        }
    }
}
