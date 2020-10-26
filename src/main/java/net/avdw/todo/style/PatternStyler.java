package net.avdw.todo.style;

import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.style.painter.IPainter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternStyler implements IPainter {
    private final Pattern pattern;
    private final String color;
    private final DefaultTextColor defaultTextColor;
    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();

    public PatternStyler(final String regex, final String color, final DefaultTextColor defaultTextColor) {
        this.pattern = Pattern.compile(regex);
        this.color = COLOR_CONVERTER.hexToAnsiFg(Integer.parseInt(color.replace("0x", ""), 16));
        this.defaultTextColor = defaultTextColor;
    }

    @Override
    public String paint(final String string, final String reset) {
        String replacedText = string;
        Matcher matcher = pattern.matcher(string);

        while (matcher.find()) {
            Logger.trace("Styling {}; '{}'", pattern.pattern(), matcher.group());
            replacedText = replacedText.replace(matcher.group(), Ansi.ansi().a(color).a(matcher.group()).a(reset).toString());
        }
        return replacedText;
    }
}
