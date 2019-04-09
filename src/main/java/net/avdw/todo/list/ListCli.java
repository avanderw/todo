package net.avdw.todo.list;

import net.avdw.todo.Config;
import net.avdw.todo.Main;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "ls", description = "List the todo.txt items.")
public class ListCli implements Runnable {
    @CommandLine.Parameters
    List<String> filters;
    
    @CommandLine.Option(names = "-p", description = "List all project tags.")
    boolean listProjects;

    @CommandLine.Option(names = "-c", description = "List all context tags.")
    boolean listContexts;

    @Override
    public void run() {
        ListTodoTxt listTodoTxt = new ListTodoTxt(Config.TODO_FILE, Main.EVENT_BUS);

        if (listProjects) {
            listTodoTxt.listProjects();
        }

        if (listContexts) {
            listTodoTxt.listContexts();
        }

        if (listProjects || listContexts) {
            return;
        }

        if (filters == null || filters.isEmpty()) {
            listTodoTxt.list();
        } else {
            listTodoTxt.list(filters);
        }
    }

    public static void main(String[] args) {
        new ListCli().run();
    }
}
