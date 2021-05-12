package net.avdw.todo.extension.moscow;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoscowMapper {

    private final MoscowTodoTxtExt moscowExt;

    @Inject
    public MoscowMapper(final MoscowTodoTxtExt moscowExt) {
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
