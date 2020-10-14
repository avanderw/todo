package net.avdw.todo.groupby;

import net.avdw.todo.domain.Todo;

import java.util.Locale;
import java.util.function.Function;

public class ContextGroupBy implements GroupBy<Todo, String, String> {
    @Override
    public Function<Todo, String> collector() {
        return t -> {
            if (t.getContextList().isEmpty()) {
                return "";
            }
            if (t.getContextList().size() > 1) {
                return String.join(", ", t.getContextList());
            } else {
                return t.getContextList().get(0);
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return selector.equals("@") || selector.toLowerCase(Locale.ENGLISH).equals("context");
    }

    @Override
    public String name() {
        return "Context";
    }
}
