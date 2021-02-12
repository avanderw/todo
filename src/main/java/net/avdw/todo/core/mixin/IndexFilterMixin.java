package net.avdw.todo.core.mixin;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;
import picocli.CommandLine.Parameters;

import java.util.Set;

public class IndexFilterMixin extends AbstractSpecification<Integer, Todo> {
    @Parameters(paramLabel = "idx", descriptionKey = "index.filter.description", split = ",", arity = "0..1", index = "0")
    private Set<Integer> idxList;

    public boolean isActive() {
        return idxList != null && !idxList.isEmpty();
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        if (idxList == null) {
            return true;
        }
        return idxList.contains(todo.getIdx());
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", this.getClass().getSimpleName(), idxList);
    }
}
