package net.avdw.todo.extension.plan;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlanMapper {

    private final PlanTodoTxtExt planExt;

    @Inject
    public PlanMapper(final PlanTodoTxtExt planExt) {
        this.planExt = planExt;
    }

    public String map(final Todo todo) {
        return planExt.getValue(todo).orElse("No");
    }

    public int mapToInt(final Todo todo) {
        return switch (map(todo)) {
            case "strategic" -> 13;
            case "tactical" -> 8;
            case "operational" -> 3;
            default -> 0;
        };
    }
}
