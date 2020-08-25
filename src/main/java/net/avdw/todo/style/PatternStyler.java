package net.avdw.todo.style;

import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternStyler implements IStyler {
    private final Pattern pattern;
    private final String color;
    private final String defaultColor;
    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();

    public PatternStyler(final String regex, final String color, final String defaultColor) {
        this.pattern = Pattern.compile(regex);
        this.color = COLOR_CONVERTER.hexToAnsiFg(Integer.parseInt(color.replace("0x", ""), 16));
        this.defaultColor = defaultColor;
    }

    @Override
    public String style(final String text) {
        String replacedText = text;
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            Logger.trace("Styling {}; '{}'", pattern.pattern(), matcher.group());
            replacedText = replacedText.replace(matcher.group(), Ansi.ansi().a(color).a(matcher.group()).a(defaultColor).toString());
        }
        return replacedText;
    }
}
