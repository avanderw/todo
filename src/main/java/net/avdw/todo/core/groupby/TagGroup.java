package net.avdw.todo.core.groupby;

import net.avdw.todo.domain.Todo;

import javax.inject.Singleton;
import java.util.function.Function;

@Singleton
public class TagGroup implements Group<Todo, String> {

    private final String selector;
    private final String tag;

    public TagGroup(final String selector) {
        this.selector = selector;
        this.tag = selector.substring(0, selector.length() - 1);
    }

    @Override
    public Function<Todo, String> collector() {
        return t -> {
            if (t.getExtValueList(tag).isEmpty()) {
                return "";
            }
            if (t.getExtValueList(tag).size() > 1) {
                return String.join(", ", t.getExtValueList(tag));
            } else {
                return t.getExtValueList(tag).get(0);
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return this.selector.equals(selector) && selector.endsWith(":");
    }

    @Override
    public String name() {
        return Character.toUpperCase(tag.charAt(0)) + tag.substring(1);
    }
}
