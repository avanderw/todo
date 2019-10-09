package net.avdw.todo.config;

import com.google.inject.Inject;
import net.avdw.todo.Ansi;
import net.avdw.todo.property.PropertyKey;
import net.avdw.todo.property.PropertyResolver;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

public class LoggingSetup {
    private final PropertyResolver propertyResolver;

    @Inject
    LoggingSetup(final PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
        setup();
    }

    private void setup() {
        String level = String.format("%s{level}%s", Ansi.BLUE, Ansi.RESET);
        String line = String.format("%s{line}%s", Ansi.YELLOW, Ansi.RESET);
        String clazz = String.format("%s{class}%s", Ansi.WHITE, Ansi.RESET);
        String method = String.format("%s{method}()%s", Ansi.CYAN, Ansi.RESET);

        String formatPattern;
        if (Boolean.parseBoolean(propertyResolver.resolve(PropertyKey.RELEASE_MODE))) {
            formatPattern = "{message}";
        } else {
            formatPattern = String.format("[%s] %s:%s:%s {message}", level, clazz, line, method);
        }

        Logger.getConfiguration()
                .formatPattern(formatPattern)
                .level(Level.valueOf(propertyResolver.resolve(PropertyKey.LOGGING_LEVEL)))
                .activate();
    }
}
