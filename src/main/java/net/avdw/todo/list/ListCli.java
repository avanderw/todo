package net.avdw.todo.list;

import net.avdw.todo.Config;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "ls", description = "List the todo.txt items.")
public class ListCli implements Runnable {
    @CommandLine.Parameters
    List<String> filters;

    @Override
    public void run() {
        ListFunc listFunc = new ListFunc(Config.TODO_FILE);
        if (filters == null || filters.isEmpty()) {
            listFunc.list();
        } else {
            listFunc.list(filters);
        }
    }

    public static void main(String[] args) {
        new ListCli().run();
    }
}
