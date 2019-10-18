package net.avdw.todo.color;

public class RGB {
    private final double r;
    private final double g;
    private final double b;

    /**
     * Constructor.
     *
     * @param r range [0..1]
     * @param g range [0..1]
     * @param b range [0..1]
     */
    public RGB(final double r, final double g, final double b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * Get the R value in the range [0..1].
     * @return r value
     */
    public double r() {
        return r;
    }

    /**
     * Get the G value in the range [0..1].
     * @return g value
     */
    public double g() {
        return g;
    }

    /**
     * Get the B value in the range [0..1].
     * @return b value
     */
    public double b() {
        return b;
    }
}
