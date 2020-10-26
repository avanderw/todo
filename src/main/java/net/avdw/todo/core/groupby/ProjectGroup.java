package net.avdw.todo.core.groupby;

import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

import java.util.Locale;
import java.util.function.Function;

@Singleton
public class ProjectGroup implements Group<Todo, String> {
    @Override
    public Function<Todo, String> collector() {
        return t -> {
            if (t.getProjectList().isEmpty()) {
                return "";
            } else if (t.getProjectList().size() == 1) {
                return t.getProjectList().get(0);
            } else {
                return String.join(", ", t.getProjectList());
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(final String selector) {
        return selector.equals("+") || selector.toLowerCase(Locale.ENGLISH).equals("project");
    }

    @Override
    public String name() {
        return "Project";
    }
}
