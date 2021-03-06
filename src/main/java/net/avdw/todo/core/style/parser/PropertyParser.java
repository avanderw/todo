package net.avdw.todo.core.style.parser;

import net.avdw.todo.Guard;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.core.style.painter.DatePainter;
import net.avdw.todo.core.style.painter.DefaultPainter;
import net.avdw.todo.core.style.painter.IPainter;
import net.avdw.todo.core.style.painter.RegexPainter;
import org.fusesource.jansi.Ansi;
import org.tinylog.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Singleton
public class PropertyParser implements IParser<IPainter> {
    private final ColorConverter colorConverter;
    private final DateKeyParser dateKeyParser;
    private final Properties properties;

    @Inject
    public PropertyParser(final Properties properties, final ColorConverter colorConverter, final DateKeyParser dateKeyParser) {
        this.properties = properties;
        this.colorConverter = colorConverter;
        this.dateKeyParser = dateKeyParser;
    }

    public Optional<IPainter> parse(final Object key) {
        if (key instanceof String) {
            return parse((String) key);
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<IPainter> parse(final String key) {
        if (key.matches("^.*color$")) {
            return Optional.empty();
        }

        final String regex = properties.getProperty(key);
        final String colorKey = String.format("%s.color", key);

        if (properties.getProperty(colorKey) == null) {
            Logger.warn("No color defined for key '{}', not creating painter", key);
            return Optional.empty();
        }

        final int colorHex = Integer.parseInt(properties.getProperty(colorKey).substring(1), 16);
        final String color = colorConverter.hexToAnsiFg(colorHex);
        if (key.matches("^regex.*(?<!color)$")) {
            Logger.debug("{}Creating {} ({}='{}'){}", color, RegexPainter.class.getSimpleName(), key, regex, Ansi.ansi().reset());
            return Optional.of(new RegexPainter(regex, color));
        } else if (key.matches("^date.*")) {
            Logger.debug("{}Creating {} ({}='{}'){}", color, DatePainter.class.getSimpleName(), key, regex, Ansi.ansi().reset());
            final Guard<Date> dateGuard = dateKeyParser.parse(key);
            return Optional.of(new DatePainter(dateGuard, regex, color));
        } else if (key.matches("^default.*")) {
            Logger.debug("{}Creating {} ({}='{}'){}", color, DefaultPainter.class.getSimpleName(), key, regex, Ansi.ansi().reset());
            return Optional.of(new DefaultPainter(regex, color));
        } else {
            throw new UnsupportedOperationException(String.format("Key '%s' is not supported in the styling property file", key));
        }
    }
}
