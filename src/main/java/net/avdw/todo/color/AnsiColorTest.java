package net.avdw.todo.color;

import net.avdw.todo.color.generator.HSLColorGenerator;
import net.avdw.todo.number.NumberInterpolater;
import net.avdw.todo.number.NumberSampler;
import net.avdw.todo.number.generator.ConstantNumberGenerator;
import net.avdw.todo.number.generator.IteratingNumberGenerator;

// http://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html#16-colors
public final class AnsiColorTest {
    public static final String BLACK = "\u001b[30;1m";
    public static final String RED = "\u001b[31;1m";
    public static final String GREEN = "\u001b[32;1m";
    public static final String YELLOW = "\u001b[33m";
    public static final String BLUE = "\u001b[34;1m";
    public static final String MAGENTA = "\u001b[35;1m";
    public static final String CYAN = "\u001b[36m";
    public static final String WHITE = "\u001b[37;1m";
    public static final String RESET = "\u001b[0m";
    public static final String PROJECT_COLOR = MAGENTA;
    public static final String CONTEXT_COLOR = CYAN;

    private AnsiColorTest() {
    }

    public static void main(final String[] args) {
        final int sampleCount = 60;
        final int sampleCountDiv4 = sampleCount / 4;
        final NumberInterpolater interpolater = new NumberInterpolater();
        final NumberSampler sampler = new NumberSampler(interpolater);
        final IteratingNumberGenerator numberGenerator = new IteratingNumberGenerator(sampler.sample(0, 120, sampleCount), true);
        final ColorConverter colorConverter = new ColorConverter();
        final HSLColorGenerator colorGenerator = new HSLColorGenerator(numberGenerator, new ConstantNumberGenerator(.75), new ConstantNumberGenerator(.5), colorConverter);
        for (int i = 0; i < sampleCount; i++) {
            final RGB rgb = colorGenerator.generateRGB();
            final int color = colorConverter.rgbToHex(rgb.r(), rgb.g(), rgb.b());
            System.out.printf("%s \u001b[0m", colorConverter.hexToAnsiBg(color));
        }
        System.out.println();
        for (int i = 0; i < sampleCount; i++) {
            if (i % sampleCountDiv4 == 0) {
                System.out.println();
            }
            final RGB rgb = colorGenerator.generateRGB();
            final int color = colorConverter.rgbToHex(rgb.r(), rgb.g(), rgb.b());
            System.out.printf("%s %3s\u001b[0m", colorConverter.hexToAnsiFg(color, true), colorConverter.rgbToHue(rgb.r(), rgb.g(), rgb.b()));
        }
    }

}
