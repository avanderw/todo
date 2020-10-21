package net.avdw.todo.core.groupby;

import net.avdw.todo.core.Guard;
import net.avdw.todo.domain.Todo;
import org.codehaus.plexus.util.StringUtils;

import java.util.function.Function;

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
            if (t.getTagValueList(tag).isEmpty()) {
                return "";
            }
            if (t.getTagValueList(tag).size() > 1) {
                return String.join(", ", t.getTagValueList(tag));
            } else {
                return t.getTagValueList(tag).get(0);
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return this.selector.equals(selector) && selector.endsWith(":");
    }

    @Override
    public String name() {
        return StringUtils.capitalise(tag);
    }
}
