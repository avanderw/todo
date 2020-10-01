package net.avdw.todo.style;

import com.google.inject.Inject;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StylerBuilder {

    private final DefaultTextColor defaultTextColor;

    @Inject
    StylerBuilder(final DefaultTextColor defaultTextColor) {
        this.defaultTextColor = defaultTextColor;
    }

    public List<IStyler> buildFrom(final Properties properties) {
        List<IStyler> stylerList = new ArrayList<>();
        properties.keySet().forEach(key -> {
            String propertyKey = key.toString();
            int firstDot = propertyKey.indexOf(".");
            int secondDot = propertyKey.indexOf(".", firstDot + 1);
            if (firstDot == -1) {
                Logger.warn("Cannot identify style type for ({})" +
                        "\n  (valid types [ 'pattern', 'date', 'int' ])" +
                        "\n  (format ['color'.]type.tag[.argument])", propertyKey);
                return;
            }

            String type = propertyKey.substring(0, firstDot);
            switch (type) {
                case "pattern" -> {
                    String regex = properties.get(propertyKey).toString();
                    String color = properties.getProperty(String.format("color.%s", propertyKey));
                    stylerList.add(new PatternStyler(regex, color, defaultTextColor));
                    Logger.debug("Creating pattern styler ({})", propertyKey);
                }
                case "color" -> Logger.trace("Ignore color property ({})" +
                        "\n  (used when creating '{}')", propertyKey, propertyKey.substring(propertyKey.indexOf(".") + 1));
                case "date" -> {
                    if (secondDot == -1) {
                        Logger.warn("Could not identify style tag ({})" +
                                "\n  (format ['color'.]type.tag[.argument])", propertyKey);
                        return;
                    }

                    String tag = propertyKey.substring(firstDot + 1, secondDot);
                    String argument = propertyKey.substring(secondDot + 1);
                    String color = properties.getProperty(propertyKey);
                    stylerList.add(new DateStyler(tag, argument, color, defaultTextColor));
                    Logger.debug("Creating date styler ({})", propertyKey);
                }
                case "int" -> {
                    if (secondDot == -1) {
                        Logger.warn("Could not identify style tag ({})" +
                                "\n  (format ['color'.]type.tag[.argument])", propertyKey);
                        return;
                    }

                    String tag = propertyKey.substring(firstDot + 1, secondDot);
                    String argument = propertyKey.substring(secondDot + 1);
                    String color = properties.getProperty(propertyKey);
                    stylerList.add(new IntStyler(tag, argument, color, defaultTextColor));
                    Logger.debug("Creating int styler ({})", propertyKey);
                }
                default -> Logger.warn("Unknown type ({})" +
                        "\n  (valid types [ 'pattern', 'date', 'int' ])" +
                        "\n  (format ['color'.]type.tag[.argument])", propertyKey);
            }
        });

        return stylerList;
    }
}
