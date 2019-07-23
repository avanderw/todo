package net.avdw.todo.action;

import net.avdw.todo.Ansi;
import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.util.Scanner;

@Command(name = "ls", aliases = "list", description = "List the items in todo.txt")
public class TodoList implements Runnable {
    @ParentCommand
    private Todo todo;

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            while (scanner.hasNext()) {
                lineNum++;
                String line = scanner.nextLine();
                Console.info(String.format("[%s%2s%s] %s", Ansi.Blue, lineNum, Ansi.Reset, new TodoItem(line)));
            }
            Console.divide();
            Console.info(String.format("%s of %s tasks shown", lineNum, lineNum));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
