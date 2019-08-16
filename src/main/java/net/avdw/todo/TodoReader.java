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
    public TodoReader(final Todo todo) {
        this.todo = todo;
    }

    /**
     * Find the todo item in the todo.txt file.
     * Take into account the visibility of items configured when determining the index.
     * Currently this method does not take into account filters.
     *
     * @param todoFile the file to search through
     * @param idx the index in the file to find
     * @return the todo item that was found
     */
    public Optional<TodoItem> readLine(final Path todoFile, final int idx) {
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
