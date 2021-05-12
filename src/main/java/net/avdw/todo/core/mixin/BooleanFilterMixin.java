package net.avdw.todo.core.mixin;

import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.AbstractSpecification;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Option;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class BooleanFilterMixin extends AbstractSpecification<Integer, Todo> implements Filter<Integer, Todo> {
    @Option(names = "--and", descriptionKey = "list.and.desc", split = ",", paramLabel = "text")
    private List<String> andFilterList = new ArrayList<>();
    @Option(names = "--not", descriptionKey = "list.not.desc", split = ",", paramLabel = "text")
    private List<String> notFilterList = new ArrayList<>();
    @Option(names = "--or", descriptionKey = "list.or.desc", split = ",", paramLabel = "text")
    private List<String> orFilterList = new ArrayList<>();

    private Specification<Integer, Todo> cache;

    public boolean isActive() {
        return !andFilterList.isEmpty() || !notFilterList.isEmpty() || !orFilterList.isEmpty();
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return specification().isSatisfiedBy(todo);
    }

    @Override
    public Specification<Integer, Todo> specification() {
        if (cache != null) {
            return cache;
        }

        Specification<Integer, Todo> specification = new Any<>();
        for (final String filter : andFilterList) {
            specification = specification.and(new IsContaining(filter));
        }
        for (final String filter : orFilterList) {
            specification = specification.or(new IsContaining(filter));
        }
        for (final String filter : notFilterList) {
            specification = specification.not(new IsContaining(filter));
        }
        cache = specification;
        return specification;
    }
}
