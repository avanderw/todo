package net.avdw.todo.render;

import net.avdw.todo.Ansi;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.color.RGB;
import net.avdw.todo.number.NumberInterpolater;

public class PercentageRenderer {
    private NumberInterpolater interpolater;
    private ColorConverter colorConverter;

    PercentageRenderer(final NumberInterpolater interpolater, final ColorConverter colorConverter) {
        this.interpolater = interpolater;
        this.colorConverter = colorConverter;
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
        String ansiColor = Ansi.getForegroundColor(color, true);
        return String.format("%s%s%s", ansiColor, text, Ansi.RESET);
    }

    public static void main(final String[] args) {
        PercentageRenderer renderer = new PercentageRenderer(new NumberInterpolater(), new ColorConverter());
        for (int i = 0; i <= 100; i++) {
            if (i % 10 == 0) {
                System.out.println();
            }
            System.out.print(String.format("%4s", renderer.renderText(i / 100.)));
        }
        System.out.println();
    }
}
