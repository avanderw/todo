package net.avdw.todo.action;

import com.google.inject.Inject;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.theme.Theme;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "top", description = "List the top items in todo.txt")
public class TodoTop implements Runnable {
    @CommandLine.Parameters(description = "How many items to show from the top", arity = "0..1")
    private int limit = 10;
    @CommandLine.Option(names = "--clean", description = "Print todo item without meta tags and index")
    private boolean cleanMeta = false;
    @CommandLine.Option(names = "--all", description = "Show completed items")
    private boolean showCompleted = false;

    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    @Working
    private Path todoPath;
    @Inject
    private Theme theme;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        List<TodoItem> filteredTodoItemList;
        if (!showCompleted) {
            filteredTodoItemList = todoItemList.stream().filter(TodoItem::isIncomplete).collect(Collectors.toList());
        } else {
            filteredTodoItemList = todoItemList;
        }

        if (todoItemList.size() > limit) {
            filteredTodoItemList = filteredTodoItemList.subList(0, limit);
        }

        if (filteredTodoItemList.isEmpty()) {
            Logger.info("The list is empty");
        }

        theme.printHeader("top");
        if (cleanMeta) {
            filteredTodoItemList.forEach(theme::printCleanTodoItemWithoutIdx);
        } else {
            filteredTodoItemList.forEach(theme::printFullTodoItemWithIdx);
        }
        theme.printDuration();
        theme.printDisplaySummary(filteredTodoItemList.size(), todoItemList.size());
    }
}
