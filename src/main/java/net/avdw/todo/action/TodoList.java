package net.avdw.todo.action;

import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Command(name = "ls", aliases = "list", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "One or more filters to apply")
    private List<String> filters;

    @Option(names = {"-a", "--all"}, description = "Show completed items")
    private boolean showAll;

    @Override
    public void run() {
        Logger.debug(String.format("Filters: %s", filters));
        if (filters == null) {
            filters = new ArrayList<>();
        }
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            int matched = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);

                if (item.isNotDone() || showAll) {
                    lineNum++;
                    if (filters.stream().allMatch(line::contains)) {
                        matched++;
                        Console.info(String.format("[%s%2s%s] %s", Ansi.Blue, lineNum, Ansi.Reset, item));
                    }
                }
            }
            Console.divide();
            Console.info(String.format("%s of %s tasks shown", matched, lineNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
