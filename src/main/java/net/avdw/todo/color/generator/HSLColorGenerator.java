package net.avdw.todo.color.generator;

import com.google.inject.Inject;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.RGB;
import net.avdw.todo.number.generator.NumberGenerator;

public class HSLColorGenerator implements ColorGenerator {
    private final NumberGenerator hueGenerator;
    private final NumberGenerator saturationGenerator;
    private final NumberGenerator luminanceGenerator;
    private final ColorConverter colorConverter;

    @Inject
    public HSLColorGenerator(final NumberGenerator hueGenerator, final NumberGenerator saturationGenerator, final NumberGenerator luminanceGenerator, final ColorConverter colorConverter) {
        this.hueGenerator = hueGenerator;
        this.saturationGenerator = saturationGenerator;
        this.luminanceGenerator = luminanceGenerator;
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
        int hue = (int) Math.max(0, Math.min(360, hueGenerator.nextValue()));
        double saturation = Math.max(0, Math.min(1, saturationGenerator.nextValue()));
        double luminance = Math.max(0, Math.min(1, luminanceGenerator.nextValue()));
        return colorConverter.hslToRgb(hue, saturation, luminance);
    }
}
