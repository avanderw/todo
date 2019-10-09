package net.avdw.todo.number;

public class DoubleNormaliser {
    public double normalise(double number, double firstBound, double lastBound) {
        double distance = Math.abs(lastBound - firstBound);
        return (number - Math.min(firstBound, lastBound)) / distance;
    }
}
