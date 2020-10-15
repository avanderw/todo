package net.avdw.todo.stats;

import lombok.Data;

@Data
public class Statistic {
    private double iqr;
    private double min;
    private double trimmedMin;
    private double q1;
    private double median;
    private double q3;
    private double trimmedMax;
    private double max;
    private long n;
    private double stdDev;
    private double mean;
    private double minOneStdDev;
    private double oneStdDev;
    private double twoStdDev;
    private double threeStdDev;
}
