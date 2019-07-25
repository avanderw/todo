package net.avdw.todo.action;

import net.avdw.todo.Console;
import net.avdw.todo.Todo;
import net.avdw.todo.TodoItem;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

class TodoReader {

    private Todo todo;

    TodoReader(Todo todo) {
        this.todo = todo;
    }

    Optional<TodoItem> readLine(int idx) {
        TodoItem readLine = null;
        try (Scanner scanner = new Scanner(todo.getTodoFile())) {
            int lineNum = 0;
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                TodoItem item = new TodoItem(line);
                if (item.isNotDone() || todo.showAll()) {
                    lineNum++;
                    if (lineNum == idx) {
                        readLine = item;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            Console.error(String.format("Error reading `%s`", todo.getTodoFile()));
            Logger.error(e);
        }

        if (readLine != null) {
            return Optional.of(readLine);
        } else {
            return Optional.empty();
        }
    }
}
