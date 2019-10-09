package net.avdw.todo.number;

public class NumberNormaliser {
    /**
     * Normalise a number into the range [0..1]
     *
     * @param number     the number to convert
     * @param upperBound the upper bound for the number
     * @return a number in the range [0..1]
     */
    public double normalise(double number, double upperBound) {
        return normalise(number, upperBound, 0);
    }

    /**
     * Normalise a number into the range [0..1]
     *
     * @param number     the number to convert
     * @param firstBound the first bound for the  number
     * @param lastBound  the last bound for the number
     * @return a number in the range [0..1]
     */
    public double normalise(double number, double firstBound, double lastBound) {
        double distance = Math.abs(lastBound - firstBound);
        return (number - Math.min(firstBound, lastBound)) / distance;
    }
}
