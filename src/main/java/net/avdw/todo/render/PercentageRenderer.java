package net.avdw.todo.render;

import com.google.inject.Inject;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.RGB;
import net.avdw.todo.number.NumberInterpolater;

public class PercentageRenderer {
    private final NumberInterpolater interpolater;
    private final ColorConverter colorConverter;
    private final AnsiColor ansiColor;

    @Inject
    PercentageRenderer(final NumberInterpolater interpolater, final ColorConverter colorConverter, final AnsiColor ansiColor) {
        this.interpolater = interpolater;
        this.colorConverter = colorConverter;
        this.ansiColor = ansiColor;
    }

    /**
     * Render the percentage as text color gradient.
     *
     * @param percentage the percentage to render
     * @return the string formatting representing the colored text
     */
    public String renderText(final Double percentage) {
        double h = interpolater.interpolate(0, 120, percentage);
        String text = String.format("%3.0f%%", percentage * 100);
        RGB color = colorConverter.hslToRgb((int) h, .75, .5);
        return String.format("%s%s%s", ansiColor.getForegroundColor(color, false), text, AnsiColor.RESET);
    }

    public static void main(final String[] args) {
        ColorConverter colorConverter =  new ColorConverter();
        PercentageRenderer renderer = new PercentageRenderer(new NumberInterpolater(), colorConverter, new AnsiColor(colorConverter));
        for (int i = 0; i <= 100; i++) {
            if (i % 10 == 0) {
                System.out.println();
            }
            System.out.print(String.format("%4s", renderer.renderText(i / 100.)));
        }
        System.out.println();
    }
}
