package net.avdw.todo.color;

public class RGB {
    private double r;
    private double g;
    private double b;

    /**
     * Constructor.
     *
     * @param r range [0..1]
     * @param g range [0..1]
     * @param b range [0..1]
     */
    public RGB(double r, double g, double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }
}
