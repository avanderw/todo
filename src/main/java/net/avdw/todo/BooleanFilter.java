package net.avdw.todo;

import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class BooleanFilter {
    @CommandLine.Parameters(descriptionKey = "list.and.desc", split = ",")
    private List<String> andFilterList = new ArrayList<>();
    @CommandLine.Option(names = "--not", descriptionKey = "list.not.desc", split = ",")
    private List<String> notFilterList = new ArrayList<>();
    @CommandLine.Option(names = "--or", descriptionKey = "list.or.desc", split = ",")
    private List<String> orFilterList = new ArrayList<>();

    public Specification<Integer, Todo> specification(Specification<Integer, Todo> specification) {
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
