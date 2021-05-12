package net.avdw.todo.extension.moscow;

import net.avdw.todo.core.selector.Selector;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;

import javax.inject.Inject;
import java.util.Comparator;

public class MoscowSelector implements Selector {
    private final MoscowTodoTxtExt moscowTodoTxtExt;
    private final MoscowMapper moscowMapper;

    @Inject
    public MoscowSelector(final MoscowMapper moscowMapper, final MoscowTodoTxtExt moscowTodoTxtExt) {
        this.moscowMapper = moscowMapper;
        this.moscowTodoTxtExt = moscowTodoTxtExt;
    }

    @Override
    public Comparator<? super Todo> comparator() {
        return Comparator.comparingInt(moscowMapper::mapToInt).reversed();
    }

    @Override
    public boolean isSatisfiedBy(final String type) {
        return moscowTodoTxtExt.getSupportedExtList().stream().anyMatch(type::contains);
    }

    @Override
    public int mapToInt(final Todo todo) {
        return moscowMapper.mapToInt(todo);
    }

    @Override
    public String replaceRegex() {
        return String.join("|", moscowTodoTxtExt.getSupportedExtList());
    }

    @Override
    public Specification<Integer, Todo> specification() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String symbol() {
        return "MoSCoW";
    }

    @Override
    public String toString() {
        return "MoscowSelector";
    }
}
