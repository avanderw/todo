package net.avdw.todo.color;

public class ColorConverter {

    /**
     * Convert RGB values to a hue using a linear transformation.
     *
     * @param r range [0..1]
     * @param g range [0..1]
     * @param b range [0..1]
     * @return hue angel in range [0..360]
     */
    public int rgbToHue(final double r, final double g, final double b) {
        double f, min, mid, max, n;
        max = Math.max(r, Math.max(g, b));
        min = Math.min(r, Math.min(g, b));

        // achromatic case
        if (max - min == 0) {
            return 0;
        }

        mid = middleValue(r, g, b);

        if (r == max) {
            n = (b == min) ? 0 : 5;
        } else if (g == max) {
            n = (b == min) ? 1 : 2;
        } else {
            n = (r == min) ? 3 : 4;
        }

        f = (n % 2 == 0) ? mid - min : max - mid;
        f = f / (max - min);

        return (int) (60 * (n + f));
    }

    /**
     * Convert HSV values to RGB values.
     *
     * @param hue        range [0..360]
     * @param saturation range [0..1]
     * @param value      range [0..1]
     * @return rgb with values in range [0..1]
     */
    public RGB hsvToRgb(final int hue, final double saturation, final double value) {
        double min = (1 - saturation) * value;
        return hueToRgb(min, value, hue);
    }

    /**
     * Convert HSL values to RGB values.
     *
     * @param hue        range [0..360]
     * @param saturation range [0..1]
     * @param luminance  range [0..1]
     * @return rgb with values in range [0..1]
     */
    public RGB hslToRgb(final int hue, final double saturation, final double luminance) {
        double delta = 0;
        if (luminance < 0.5) {
            delta = saturation * luminance;
        } else {
            delta = saturation * (1 - luminance);
        }

        return hueToRgb(luminance - delta, luminance + delta, hue);
    }

    /**
     * Convert hue to RGB values using a linear transformation.
     *
     * @param min of R,G,B
     * @param max of R,G,B
     * @param hue angle in range [0..360]
     * @return rgb with values in range [0..1]
     */
    public RGB hueToRgb(final double min, final double max, final int hue) {
        final int maxHue = 360;
        int n, h = hue;
        double mu, md, f;
        while (h < 0) {
            h += maxHue;
        }

        n = (int) Math.floor(h / 60.);
        f = (h - n * 60.) / 60.;
        n %= 6;

        mu = min + ((max - min) * f);
        md = max - ((max - min) * f);

        switch (n) {
            case 0:
                return new RGB(max, mu, min);
            case 1:
                return new RGB(md, max, min);
            case 2:
                return new RGB(min, max, mu);
            case 3:
                return new RGB(min, md, max);
            case 4:
                return new RGB(mu, min, max);
            case 5:
                return new RGB(max, min, md);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private double middleValue(final double a, final double b, final double c) {
        if (a > b && a > c) {
            return Math.max(b, c);
        } else if (b > a && b > c) {
            return Math.max(a, c);
        } else if (a > b) {
            return a;
        } else {
            return c;
        }
    }
}
