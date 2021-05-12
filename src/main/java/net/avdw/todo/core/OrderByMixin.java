package net.avdw.todo.core;

import net.avdw.todo.core.selector.ExtSelector;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrderByMixin {
    private final Set<Selector> baseSelectorSet;
    private Set<Selector> extendedSelectorSet;
    @Option(names = "--order-by")
    private String orderBy;
    private TodoEvaluator todoEvaluator;

    @Inject
    OrderByMixin(final Set<Selector> baseSelectorSet) {
        this.baseSelectorSet = baseSelectorSet;
    }

    private Comparator<? super Todo> comparator() {
        final Set<Selector> satisfiedSelectorSet = extendedSelectorSet.stream()
                .filter(selector -> selector.isSatisfiedBy(orderBy))
                .collect(Collectors.toSet());
        return switch (satisfiedSelectorSet.size()) {
            case 0 -> Comparator.naturalOrder();
            case 1 -> satisfiedSelectorSet.iterator().next().comparator();
            default -> Comparator.comparingInt((Todo todo) -> todoEvaluator.evaluate(todo)).reversed();
        };
    }

    private void init() {
        if (todoEvaluator == null) {
            extendedSelectorSet = new HashSet<>(baseSelectorSet);
            final String regex = "\\S+:";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(orderBy);
            while (matcher.find()) {
                extendedSelectorSet.add(new ExtSelector(matcher.group()));
            }

            todoEvaluator = new TodoEvaluator(orderBy, extendedSelectorSet);
        }
    }

    private boolean isActive() {
        return orderBy != null;
    }

    private boolean isValid() {
        if (isActive()) {
            return todoEvaluator.isValid();
        }
        throw new UnsupportedOperationException("Not active");
    }

    public void order(final List<Todo> todoList) {
        if (isActive()) {
            init();

            if (isValid()) {
                todoList.sort(comparator());
            } else {
                throw new UnsupportedOperationException("[--order-by] uses an unsupported selector");
            }
        }
    }
}
