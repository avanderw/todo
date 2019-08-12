package net.avdw.todo;

import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class TodoReader {

    private Todo todo;

    @Inject
    public TodoReader(Todo todo) {
        this.todo = todo;
    }

    public Optional<TodoItem> readLine(Path todoFile, int idx) {
        TodoItem readLine = null;
        try (Scanner scanner = new Scanner(todoFile)) {
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
            Console.error(String.format("Error reading `%s`", todoFile));
            Logger.error(e);
        }

        if (readLine != null) {
            return Optional.of(readLine);
        } else {
            return Optional.empty();
        }
    }
}
