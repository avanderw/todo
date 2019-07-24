package net.avdw.todo.admin;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Command(name = "sort", description = "Sort todo.txt")
public class TodoSort  implements Runnable{
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        List<String> todos = new ArrayList<>();
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            while (scanner.hasNext()) {
                String line=scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    todos.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String contents = todos.stream().sorted().reduce("", (orig, item)-> orig + item + "\n");
        try {
            Files.write(todo.getTodoFile(), contents.getBytes());
            Console.info("Sorted items");
            Console.divide();
            Console.info(String.format("Wrote %s", todo.getTodoFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
