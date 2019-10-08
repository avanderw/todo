package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Todo;
import net.avdw.todo.action.TodoList;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Command(name = "sort", description = "Sort todo.txt")
public class TodoSort implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    private TodoList list;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<String> todos = new ArrayList<>();
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    todos.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contents = todos.stream().sorted().reduce("", (orig, item) -> orig + item + "\n");
        try {
            Files.write(todo.getTodoFile(), contents.getBytes());
            Logger.info("Sorted items");
            Logger.info(String.format("Wrote %s", todo.getTodoFile()));
            Logger.info("---");
            list.run();
        } catch (IOException e) {
            Logger.error(String.format("Error sorting todo %s because %s", todo.getTodoFile(), e.getMessage()));
            Logger.debug(e);
        }
    }
}
