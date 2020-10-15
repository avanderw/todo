package net.avdw.todo.filters;

import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.ArrayList;
import java.util.List;

public class BooleanFilterMixin implements Filter<Integer, Todo> {
    @Parameters(descriptionKey = "list.and.desc", split = ",", paramLabel = "text")
    private List<String> andFilterList = new ArrayList<>();
    @Option(names = "--not", descriptionKey = "list.not.desc", split = ",", paramLabel = "text")
    private List<String> notFilterList = new ArrayList<>();
    @Option(names = "--or", descriptionKey = "list.or.desc", split = ",", paramLabel = "text")
    private List<String> orFilterList = new ArrayList<>();

    @Override
    public Specification<Integer, Todo> specification(final Specification<Integer, Todo> specification) {
        Specification<Integer, Todo> spec = specification;
        for (String filter : andFilterList) {
            spec = spec.and(new IsContaining(filter));
        }
        for (String filter : orFilterList) {
            spec = spec.or(new IsContaining(filter));
        }
        for (String filter : notFilterList) {
            spec = spec.not(new IsContaining(filter));
        }
        return spec;
    }
}
