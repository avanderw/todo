package net.avdw.todo.groupby;

import net.avdw.todo.domain.Todo;
import org.codehaus.plexus.util.StringUtils;

import java.util.function.Function;

public class TagGroupBy implements GroupBy<Todo, String, String> {

    private final String tag;

    public TagGroupBy(final String tag) {
        this.tag = tag;
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
        return selector.endsWith(":");
    }

    @Override
    public String name() {
        return StringUtils.capitalise(tag);
    }
}
