package net.avdw.todo.style;

import net.avdw.todo.PropertyFile;
import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Properties;

public class StyleApplicator {

    private final String defaultColor;
    private final List<IStyler> stylerList;

    StyleApplicator(final String defaultColor, final List<IStyler> stylerList) {
        this.defaultColor = defaultColor;
        this.stylerList = stylerList;
    }

    public static void main(final String[] args) {
        PropertyFile propertyFile = new PropertyFile("net.avdw/todo");
        Properties properties = propertyFile.read("style");
        String defaultColor = properties.getProperty("color.default") == null
                ? Ansi.ansi().reset().toString()
                : new ColorConverter().hexToAnsiFg(Integer.parseInt(properties.getProperty("color.default").replace("0x", ""), 16));
        StylerBuilder stylerBuilder = new StylerBuilder(defaultColor);
        StyleApplicator styleApplicator = new StyleApplicator(defaultColor, stylerBuilder.buildFrom(properties));
        System.out.println(styleApplicator.apply("2345-32-34 something due:2054-75-98"));
        System.out.println(styleApplicator.apply("x 2010-20-03 3020-03-43 importance:1 importance:2 importance:3 importance:5 importance:8 importance:13 importance:21"));
        System.out.println(styleApplicator.apply("something avanderw@gmail.com @email.com gap then +project-3"));
        System.out.println(styleApplicator.apply("something @context-1 +something else @context2"));
        System.out.println(styleApplicator.apply("something @context-1 something +else @context2 final something"));
    }

    public String apply(final String text) {
        String styledText = text;
        for (IStyler styler : stylerList) {
            styledText = styler.style(styledText);
        }
        return Ansi.ansi().a(defaultColor).a(styledText).reset().toString();
    }
}
