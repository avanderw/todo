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

        return switch (n) {
            case 0 -> new RGB(max, mu, min);
            case 1 -> new RGB(md, max, min);
            case 2 -> new RGB(min, max, mu);
            case 3 -> new RGB(min, md, max);
            case 4 -> new RGB(mu, min, max);
            case 5 -> new RGB(max, min, md);
            default -> throw new UnsupportedOperationException();
        };
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

    /**
     * Convert a hex value to an RGB object.
     *
     * @param hex integer color value
     * @return rgb with values in range [0..1]
     */
    public RGB hexToRGB(final int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;

        return new RGB(r / 255., g / 255., b / 255.);
    }

    /**
     * Convert r, g, b into a hex value.
     *
     * @param r red with values in range [0..1]
     * @param g green with values in range [0..1]
     * @param b blue with values in range [0..1]
     * @return hex value representing the color
     */
    public int rgbToHex(final double r, final double g, final double b) {
        int hexR = (int) (r * 0xFF) << 16;
        int hexG = (int) (g * 0xFF) << 8;
        int hexB = (int) (b * 0xFF);

        return hexR | hexG | hexB;
    }

    /**
     * Convert hex to ansi foreground string.
     *
     * @param hex  hex color to convert
     * @return the ansi string
     */
    public String hexToAnsiFg(final int hex) {
        return hexToAnsiFg(hex, false);
    }

    /**
     * Convert hex to ansi foreground string.
     *
     * @param hex  hex color to convert
     * @param bold whether to bold the text
     * @return the ansi string
     */
    public String hexToAnsiFg(final int hex, final boolean bold) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;

        return bold
                ? String.format("\u001b[1;38;2;%s;%s;%sm", r, g, b)
                : String.format("\u001b[0;38;2;%s;%s;%sm", r, g, b);
    }

    /**
     * Convert hex to ansi background string.
     *
     * @param hex the color to convert
     * @return the ansi string
     */
    public String hexToAnsiBg(final int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;

        return String.format("\u001b[48;2;%s;%s;%sm", r, g, b);
    }
}
