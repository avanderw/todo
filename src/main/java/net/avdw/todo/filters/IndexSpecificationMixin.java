package net.avdw.todo.filters;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;
import picocli.CommandLine.Parameters;

import java.util.Set;

public class IndexSpecificationMixin extends AbstractSpecification<Integer, Todo> {
    @Parameters(descriptionKey = "index.filter.description", split = ",")
    private Set<Integer> idxList;

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
