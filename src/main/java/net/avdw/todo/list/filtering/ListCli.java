package net.avdw.todo.list.filtering;

import com.google.inject.Inject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "filtering", aliases = "ls", description = "List the todo.txt items.")
public class ListCli implements Runnable {
    @CommandLine.Parameters
    private List<String> filters;

    @CommandLine.Option(names = "-p", description = "List all project tags.")
    private boolean listProjects;

    @CommandLine.Option(names = "-c", description = "List all context tags.")
    private boolean listContexts;

    @Inject
    private ListApi listApi;

    @Override
    public void run() {
        if (listProjects) {
            listApi.listProjects();
        }

        if (listContexts) {
            listApi.listContexts();
        }

        if (listProjects || listContexts) {
            return;
        }

        if (filters == null || filters.isEmpty()) {
            listApi.list();
        } else {
            listApi.list(filters);
        }
    }
}
