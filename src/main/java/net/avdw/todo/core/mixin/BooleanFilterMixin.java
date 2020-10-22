package net.avdw.todo.core.mixin;

import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;

public class BooleanFilterMixin implements Filter<Integer, Todo> {
    @Option(names = "--and", descriptionKey = "list.and.desc", split = ",", paramLabel = "text")
    private List<String> andFilterList = new ArrayList<>();
    @Option(names = "--not", descriptionKey = "list.not.desc", split = ",", paramLabel = "text")
    private List<String> notFilterList = new ArrayList<>();
    @Option(names = "--or", descriptionKey = "list.or.desc", split = ",", paramLabel = "text")
    private List<String> orFilterList = new ArrayList<>();

    @Override
    public Specification<Integer, Todo> specification() {
        Specification<Integer, Todo> specification = new Any<>();
        for (String filter : andFilterList) {
            specification = specification.and(new IsContaining(filter));
        }
        for (String filter : orFilterList) {
            specification = specification.or(new IsContaining(filter));
        }
        for (String filter : notFilterList) {
            specification = specification.not(new IsContaining(filter));
        }
        return specification;
    }
}
