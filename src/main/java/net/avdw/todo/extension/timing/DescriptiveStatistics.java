package net.avdw.todo.extension.timing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DescriptiveStatistics {
    private boolean isSorted = false;
    private final List<Long> valueList = new ArrayList<>();

    public void addValue(long l) {
        valueList.add(l);
        isSorted = false;
    }

    public double getPercentile(int i) {
        sort();
        int idx = valueList.size()*i/100;
        return getIdx(idx);
    }

    private double getIdx(int idx) {
        if (valueList.size() >  idx && idx >= 0) {
            return valueList.get(idx);
        } else {
            return 0;
        }
    }

    private void sort() {
        if (!isSorted) {
            Collections.sort(valueList);
            isSorted = true;
        }
    }

    public double getMin() {
        sort();
        return getIdx(0);
    }

    public double getMax() {
        sort();
        return getIdx(valueList.size()-1);
    }

    public long getN() {
        return valueList.size();
    }

    public double getStandardDeviation() {
        // Variance
        double variance = valueList.stream()
                .map(i -> i - getMean())
                .map(i -> i*i)
                .mapToDouble(i -> i).average().orElse(0.0);

        //Standard Deviation
        return Math.sqrt(variance);
    }

    public double getMean() {
        return valueList.stream()
                .mapToDouble(d -> d)
                .average()
                .orElse(0.0);
    }
}
