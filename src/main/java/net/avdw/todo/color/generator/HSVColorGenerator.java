package net.avdw.todo.color.generator;

import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.RGB;
import net.avdw.todo.number.generator.NumberGenerator;

public class HSVColorGenerator implements ColorGenerator {
    private final NumberGenerator hueGenerator;
    private final NumberGenerator saturationGenerator;
    private final NumberGenerator valueGenerator;
    private final ColorConverter colorConverter;

    HSVColorGenerator(final NumberGenerator hueGenerator, final NumberGenerator saturationGenerator, final NumberGenerator valueGenerator, final ColorConverter colorConverter) {
        this.hueGenerator = hueGenerator;
        this.saturationGenerator = saturationGenerator;
        this.valueGenerator = valueGenerator;
        this.colorConverter = colorConverter;
    }

    /**
     * Generate a color in the RGB format.
     * R in range [0..1]
     * G in range [0..1]
     * B in range [0..1]
     *
     * @return color in RGB
     */
    @Override
    public RGB generateRGB() {
        final int hue = (int) Math.max(0, Math.min(360, hueGenerator.nextValue()));
        final double saturation = Math.max(0, Math.min(1, saturationGenerator.nextValue()));
        final double value = Math.max(0, Math.min(1, valueGenerator.nextValue()));
        return colorConverter.hsvToRgb(hue, saturation, value);
    }
}
