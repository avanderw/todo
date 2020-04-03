package net.avdw.todo.chart;


import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;
import com.github.freva.asciitable.HorizontalAlign;
import com.google.inject.Inject;
import net.avdw.todo.Working;
import net.avdw.todo.file.TodoFileReader;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.render.TodoItemPrinter;
import net.avdw.todo.theme.ColorPalette;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.Frequency;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandLine.Command(name = "chart", description = "Chart a pivot of data")
public class TodoChart implements Runnable {
    @CommandLine.Parameters(description = "First key to extract", arity = "1", index = "0")
    private String firstKey;
    @CommandLine.Parameters(description = "Second key to extract", arity = "0..1", index = "1")
    private String secondKey;
    @CommandLine.Parameters(description = "Third key to extract", arity = "0..1", index = "2")
    private String thirdKey;

    @Inject
    private ColorPalette<String> colorPalette;
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
                .map(item -> item.getMetaValueFor(firstKey))
                .sorted()
                .collect(Collectors.toList());
        List<String> colValueList = filteredTodoItemList.stream()
                .map(item -> item.getMetaValueFor(secondKey))
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


        List<MetaDataValueFrequency> data = new ArrayList<>();
        frequency.valuesIterator().forEachRemaining(value -> {
            data.add(new MetaDataValueFrequency(value.toString(), frequency.getCount(value)));
        });

        try {
            data.sort(Comparator.comparing(struct -> Integer.parseInt(struct.value)));
        } catch (RuntimeException e) {
            data.sort(Comparator.comparing(struct -> struct.value));
        }

        Character[] borderStyles = AsciiTable.BASIC_ASCII_NO_DATA_SEPARATORS_NO_OUTSIDE_BORDER;
        System.out.print(colorPalette.primaryTone());
        System.out.println(AsciiTable.getTable(borderStyles, data, Arrays.asList(
                new Column().headerAlign(HorizontalAlign.CENTER).header(firstKey).with(struct -> struct.value),
                new Column().header("frequency").dataAlign(HorizontalAlign.CENTER).with(struct -> String.valueOf(struct.count)),
                new Column().dataAlign(HorizontalAlign.LEFT).with(struct -> StringUtils.repeat("#", Math.toIntExact(struct.count)))
        )));
    }

    private static class MetaDataValueFrequency {
        private final String value;
        private final long count;

        MetaDataValueFrequency(final String value, final long count) {
            this.value = value;
            this.count = count;
        }
    }
}
