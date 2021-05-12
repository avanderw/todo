package net.avdw.todo.core.style;

import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.core.style.painter.IDefaultPainter;
import net.avdw.todo.core.style.painter.IPainter;
import net.avdw.todo.core.style.parser.PropertyParser;
import net.avdw.todo.domain.Todo;
import org.fusesource.jansi.Ansi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

@Singleton
public class TodoStyler {
    private final CleanMixin cleanMixin;
    private final List<IDefaultPainter> defaultPainterList;
    private final List<IPainter> painterList;

    @Inject
    TodoStyler(final Properties properties, final PropertyParser propertyParser, final CleanMixin cleanMixin) {
        this.cleanMixin = cleanMixin;
        painterList = properties.keySet().stream()
                .map(propertyParser::parse)
                .filter(Optional::isPresent)
                .map(Optional::orElseThrow)
                .sorted(Comparator.comparing(iPainter -> iPainter.getClass().getSimpleName()).reversed())
                .collect(Collectors.toList());
        defaultPainterList = painterList.stream()
                .filter(iPainter -> iPainter instanceof IDefaultPainter)
                .map(iPainter -> (IDefaultPainter) iPainter)
                .collect(Collectors.toList());
        painterList.removeAll(defaultPainterList);
    }

    private String getDefaultColor(final Todo todo) {
        final String defaultColor = Ansi.ansi().reset().toString();
        final List<IDefaultPainter> satisfiedDefaultPainterList = defaultPainterList.stream()
                .filter(iDefaultPainter -> iDefaultPainter.isSatisfiedBy(todo))
                .collect(Collectors.toList());

        if (satisfiedDefaultPainterList.isEmpty()) {
            return defaultColor;
        } else if (satisfiedDefaultPainterList.size() > 1) {
            final List<IDefaultPainter> withoutFallBackList = satisfiedDefaultPainterList.stream()
                    .filter(iDefaultPainter -> !iDefaultPainter.isFallback())
                    .collect(Collectors.toList());
            if (withoutFallBackList.size() > 1) {
                throw new UnsupportedOperationException();
            }
            return withoutFallBackList.get(0).color();
        } else {
            return satisfiedDefaultPainterList.get(0).color();
        }
    }

    public String style(final Todo todo) {
        String text = cleanMixin.clean(todo);
        final String defaultColor = getDefaultColor(todo);

        for (final IPainter iPainter : painterList) {
            text = String.format("%s%s", defaultColor, iPainter.paint(text, defaultColor));
        }
        text += Ansi.ansi().reset().toString();
        return text;
    }
}
