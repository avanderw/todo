package net.avdw.todo.color.generator;

import net.avdw.todo.color.RGB;
import net.avdw.todo.number.generator.NumberGenerator;

public class RGBColorGenerator implements ColorGenerator {
    private final NumberGenerator redGenerator;
    private final NumberGenerator greenGenerator;
    private final NumberGenerator blueGenerator;

    RGBColorGenerator(NumberGenerator redGenerator, NumberGenerator greenGenerator, NumberGenerator blueGenerator) {
        this.redGenerator = redGenerator;
        this.greenGenerator = greenGenerator;
        this.blueGenerator = blueGenerator;
    }

    @Override
    public RGB generateRGB() {
        double red = Math.max(0, Math.min(1, redGenerator.nextValue()));
        double green = Math.max(0, Math.min(1, greenGenerator.nextValue()));
        double blue = Math.max(0, Math.min(1, blueGenerator.nextValue()));
        return new RGB(red, green, blue);
    }
}
