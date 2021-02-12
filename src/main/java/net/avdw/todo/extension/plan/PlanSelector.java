package net.avdw.todo.extension.plan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import java.util.Comparator;

@Singleton
public class PlanSelector implements Selector {
    private final PlanMapper planMapper;
    private final PlanTodoTxtExt planTodoTxtExt;

    @Inject
    public PlanSelector(final PlanMapper planMapper, final PlanTodoTxtExt planTodoTxtExt) {
        this.planMapper = planMapper;
        this.planTodoTxtExt = planTodoTxtExt;
    }

    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparingInt(planMapper::mapToInt).reversed();
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return planTodoTxtExt.getSupportedExtList().stream().anyMatch(type::contains);
    }

    @Override
    public int mapToInt(final Todo todo) {
        return planMapper.mapToInt(todo);
    }


    public String replaceRegex() {
        return String.join("|", planTodoTxtExt.getSupportedExtList());
    }

    @Override
    public Specification<Integer, Todo> specification() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String symbol() {
        return "plan";
    }
}
