package net.avdw.todo.color;

import com.google.inject.Inject;

import java.util.function.Function;

public class ColorInterpolator {
    private final ColorConverter colorConverter;

    @Inject
    ColorInterpolator(final ColorConverter colorConverter) {
        this.colorConverter = colorConverter;
    }

    /**
     * Interpolate between two hex colors.
     * This method decomposes the hex into r, g, b
     * Then interpolates each channel independently
     *
     * @param fromColor             the color to interpolate from
     * @param toColor               the color to interpolate to
     * @param percentage            the percentage to interpolate
     * @param interpolationFunction the function to modify the percentage by
     * @return the new color interpolated between the two colors
     */
    public int interpolate(final int fromColor, final int toColor, final double percentage, final Function<Double, Double> interpolationFunction) {
        RGB fromRgb = colorConverter.hexToRGB(fromColor);
        RGB toRgb = colorConverter.hexToRGB(toColor);
        double interpolation = interpolationFunction.apply(percentage);
        double deltaR = toRgb.r() - fromRgb.r();
        double deltaG = toRgb.g() - fromRgb.g();
        double deltaB = toRgb.b() - fromRgb.b();

        double newR = fromRgb.r() + deltaR * interpolation;
        double newG = fromRgb.g() + deltaG * interpolation;
        double newB = fromRgb.b() + deltaB * interpolation;

        return colorConverter.rgbToHex(newR, newG, newB);
    }
}
