package net.avdw.todo.list;

import com.google.inject.Inject;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "list", aliases = "ls", description = "List the todo.txt items.")
public class ListCli implements Runnable {
    @CommandLine.Parameters
    List<String> filters;
    
    @CommandLine.Option(names = "-p", description = "List all project tags.")
    boolean listProjects;

    @CommandLine.Option(names = "-c", description = "List all context tags.")
    boolean listContexts;

    @Inject
    ListApi listApi;

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
