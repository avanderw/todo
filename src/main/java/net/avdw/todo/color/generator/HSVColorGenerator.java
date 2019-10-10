package net.avdw.todo.color.generator;

import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.RGB;
import net.avdw.todo.number.generator.NumberGenerator;

public class HSVColorGenerator implements ColorGenerator {
    private final NumberGenerator hueGenerator;
    private final NumberGenerator saturationGenerator;
    private final NumberGenerator valueGenerator;
    private final ColorConverter colorConverter;

    HSVColorGenerator(NumberGenerator hueGenerator, NumberGenerator saturationGenerator, NumberGenerator valueGenerator, ColorConverter colorConverter) {
        this.hueGenerator = hueGenerator;
        this.saturationGenerator = saturationGenerator;
        this.valueGenerator = valueGenerator;
        this.colorConverter = colorConverter;
    }

    @Override
    public RGB generateRGB() {
        int hue = (int) Math.max(0, Math.min(360, hueGenerator.nextValue()));
        double saturation = Math.max(0, Math.min(1, saturationGenerator.nextValue()));
        double value = Math.max(0, Math.min(1, valueGenerator.nextValue()));
        return colorConverter.hsvToRgb(hue, saturation, value);
    }
}
