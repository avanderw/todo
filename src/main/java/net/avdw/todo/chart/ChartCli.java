package net.avdw.todo.chart;


import com.google.inject.Inject;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.render.TodoItemPrinter;
import org.apache.commons.math3.stat.Frequency;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "chart", description = "Chart a pivot of data")
public class ChartCli implements Runnable {
    @CommandLine.Parameters(description = "First key to extract", arity = "1", index = "0")
    private String firstKey;
    @CommandLine.Parameters(description = "Second key to extract", arity = "0..1", index = "1")
    private String secondKey;
    @CommandLine.Parameters(description = "Second key to extract", arity = "0..1", index = "2")
    private String thirdKey;

    @Inject
    private TodoFileReader todoFileReader;
    @Inject
    private TodoItemPrinter todoItemPrinter;
    @Inject
    @Working
    private Path todoPath;


    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        if (secondKey == null) {
            showBarChart();
        } else if (thirdKey == null) {
            showScatterChart();
        } else {
            showBubbleChart();
        }
    }

    private void showBubbleChart() {
        throw new UnsupportedOperationException();
    }

    private void showScatterChart() {
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        String key1SearchTerm = String.format("%s:", firstKey);
        String key2SearchTerm = String.format("%s:", secondKey);
        List<TodoItem> filteredTodoItemList = todoItemList.stream()
                .filter((item) -> item.getRawValue().contains(key1SearchTerm))
                .filter((item) -> item.getRawValue().contains(key2SearchTerm))
                .collect(Collectors.toList());

        List<String> rowValueList = filteredTodoItemList.stream()
                .map(item-> item.getMetaValueFor(firstKey))
                .sorted()
                .collect(Collectors.toList());
        List<String> colValueList = filteredTodoItemList.stream()
                .map(item->item.getMetaValueFor(secondKey))
                .sorted()
                .collect(Collectors.toList());

        System.out.println(rowValueList);
        System.out.println(colValueList);

        filteredTodoItemList.forEach(todoItemPrinter::printWithIndex);
    }

    private void showBarChart() {
        List<TodoItem> todoItemList = todoFileReader.readAll(todoPath);
        String keySearchTerm = String.format("%s:", firstKey);
        Frequency frequency = new Frequency();
        todoItemList.stream()
                .filter((item) -> item.getRawValue().contains(keySearchTerm))
                .map(item -> item.getRawValue().substring(item.getRawValue().indexOf(keySearchTerm) + keySearchTerm.length()))
                .map(string -> {
                    int index = string.indexOf(" ");
                    if (index == -1) {
                        return string;
                    } else {
                        return string.substring(0, index);
                    }
                })
                .forEach(frequency::addValue);

        System.out.println(frequency);
    }
}
