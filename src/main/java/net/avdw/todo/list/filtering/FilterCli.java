package net.avdw.todo.list.filtering;

import com.google.inject.Inject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "filter", aliases = "ls", description = "Filter the todo.txt repository tasks.")
public class FilterCli implements Runnable {
    @CommandLine.Parameters
    private List<String> filters;

    @CommandLine.Option(names = "-p", description = "List all project tags.")
    private boolean listProjects;

    @CommandLine.Option(names = "-c", description = "List all context tags.")
    private boolean listContexts;

    @Inject @Context
    private AFilter contextFilter;
    @Inject @TodoList
    private AFilter listFilter;
    @Inject @Project
    private AFilter projectFilter;

    @Override
    public void run() {

        if (listProjects) {
            projectFilter.list().forEach(System.out::println);
        }

        if (listContexts) {
            contextFilter.list().forEach(System.out::println);
        }

        if (listProjects || listContexts) {
            return;
        }

        if (filters == null || filters.isEmpty()) {
            listFilter.list().forEach(System.out::println);
        } else {
            listFilter.list(filters).forEach(System.out::println);
        }
    }
}