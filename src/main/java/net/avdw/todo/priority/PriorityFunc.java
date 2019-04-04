package net.avdw.todo.priority;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class PriorityFunc {
    private final File todoFile;

    public PriorityFunc(File todoFile) {

        this.todoFile = todoFile;
    }

    public void add(Integer idx, String priority) {
        changeTodo(Collections.singletonList(idx), priority);
    }

    public void add(List<Integer> idxs, String priority) {
        changeTodo(idxs, priority);
    }

    public void remove(Integer idx) {
        remove(Collections.singletonList(idx));
    }

    public void remove(List<Integer> idxs) {
        changeTodo(idxs, null);
    }

    private void changeTodo(List<Integer> idxs, String priority) {
        List<String> todoIdxs = idxs.stream().map(idx->String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"))).collect(Collectors.toList());
        List<String> changedLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    int finalCount = count;
                    if (idxs.stream().anyMatch(idx -> idx == finalCount)) {
                        if (priority == null) {
                            line = line.replaceFirst("^\\([A-Z]\\) ", "");
                        } else {
                            line = String.format("(%s) %s", priority, line);
                        }
                        changedLines.add(line);
                        sb.append(line).append("\n");
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }

        try {
            Files.copy(todoFile.toPath(), Paths.get(todoFile.toString() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            Files.write(todoFile.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Logger.error(e);
        }

        for (int idx = 0; idx < changedLines.size(); idx++) {
            if (priority == null) {
                System.out.print(String.format("Removed Priority: %s %s%n", todoIdxs.get(idx), changedLines.get(idx)));
            } else {
                System.out.print(String.format("Added Priority: %s %s%n", todoIdxs.get(idx), changedLines.get(idx)));
            }
        }
    }
}
