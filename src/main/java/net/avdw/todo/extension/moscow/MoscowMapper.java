package net.avdw.todo.extension.moscow;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class MoscowMapper {

    private final MoscowExt moscowExt;

    @Inject
    public MoscowMapper(final MoscowExt moscowExt) {
        this.moscowExt = moscowExt;
    }

    public Integer mapToInt(final Todo todo) {
        return switch (map(todo)) {
            case "must" -> 13;
            case "should" -> 8;
            case "could" -> 3;
            case "wont" -> 1;
            default -> 0;
        };
    }

    public String map(final Todo todo) {
        return moscowExt.getValue(todo).orElse("No");
    }
}
