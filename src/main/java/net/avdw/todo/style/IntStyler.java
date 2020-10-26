package net.avdw.todo.style;

import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.style.painter.IPainter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntStyler implements IPainter {
    private static final Pattern ARGUMENT_REGEX = Pattern.compile("(-?\\d+)([+-]?)");
    private static final ColorConverter COLOR_CONVERTER = new ColorConverter();
    private final String tag;
    private final String color;
    private final DefaultTextColor defaultTextColor;
    private boolean ascend;
    private boolean exact;
    private int arg;

    public IntStyler(final String tag, final String argument, final String color, final DefaultTextColor defaultTextColor) {
        this.tag = tag;
        this.color = COLOR_CONVERTER.hexToAnsiFg(Integer.parseInt(color.replace("0x", ""), 16));
        this.defaultTextColor = defaultTextColor;

        Matcher matcher = ARGUMENT_REGEX.matcher(argument);
        if (matcher.find()) {
            arg = Integer.parseInt(matcher.group(1));
            exact = "".equals(matcher.group(2));
            ascend = !exact && matcher.group(2).equals("+");
        }
    }

    @Override
    public String paint(final String string, final String reset) {
        Pattern pattern = Pattern.compile(String.format("(%s:(\\d+))", tag));
        Matcher matcher = pattern.matcher(string);
        String replacedText = string;

//        String defaultColor = defaultTextColor.getFromText(string);
        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(2));
            if (exact) {
                if (value == arg) {
                    Logger.trace("Styling {}; {} == {}", matcher.group(), value, arg);
                    replacedText = replacedText.replaceFirst(matcher.group(), Ansi.ansi().a(color).a(matcher.group()).a(reset).toString());
                }
            } else if (ascend) {
                if (value >= arg) {
                    Logger.trace("Styling {}; {} >= {}", matcher.group(), value, arg);
                    replacedText = replacedText.replaceFirst(matcher.group(), Ansi.ansi().a(color).a(matcher.group()).a(reset).toString());
                }
            } else {
                if (value <= arg) {
                    Logger.trace("Styling {}; {} <= {}", matcher.group(), value, arg);

                    replacedText = replacedText.replaceFirst(matcher.group(), Ansi.ansi().a(color).a(matcher.group()).a(reset).toString());
                }
            }
        }
        return replacedText;
    }
}
