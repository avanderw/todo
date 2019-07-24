package net.avdw.todo.action;

import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Command(name = "do", description = "Complete a todo item")
public class TodoDone implements Runnable {
    @ParentCommand
    private Todo todo;

    @Parameters(description = "Index to complete", arity = "1")
    private int idx;

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            TodoItem item = null;
            String lineToComplete = null;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();

                item = new TodoItem(line);

                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (lineNum == idx) {
                        lineToComplete = line;
                        break;
                    }
                }
            }

            if (lineToComplete == null) {
                Console.error("Could not find index");
            } else if (item.isDone()) {
                Console.info(String.format("[%s%s%s] %s", Ansi.Blue, idx, Ansi.Reset, item));
                Console.divide();
                Console.error("Item is already marked as done");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String completeLine = String.format("x %s %s", sdf.format(new Date()), lineToComplete.replaceFirst("\\([A-Z]\\) ", ""));
                Console.info(String.format("[%s%s%s]: %s", Ansi.Blue, lineNum, Ansi.Reset, item));
                Console.divide();
                Console.info(String.format("%s", new TodoItem(completeLine)));

                String contents = new String(Files.readAllBytes(todo.getTodoFile()));
                Files.write(todo.getTodoFile(), contents.replace(lineToComplete, completeLine).getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
