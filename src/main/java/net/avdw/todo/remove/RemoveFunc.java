package net.avdw.todo.remove;

import com.google.common.eventbus.EventBus;
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

public class RemoveFunc {
    private File file;
    private EventBus eventBus;

    public RemoveFunc(File file, EventBus eventBus) {
        this.file = file;
        this.eventBus = eventBus;
    }

    public void remove(Integer idx) {
        remove(Collections.singletonList(idx));
    }

    public void remove(List<Integer> idxs) {
        List<String> todoIdxs = idxs.stream().map(idx->String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"))).collect(Collectors.toList());
        List<String> removedLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line =scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    int finalCount = count;
                    if (idxs.stream().anyMatch(idx->idx == finalCount)) {
                        removedLines.add(line);
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }

        if (removedLines.isEmpty()) {
            System.out.println("There are no todo items.");
            return;
        }

        try {
            Files.copy(file.toPath(), Paths.get(file.toString()+".bak"), StandardCopyOption.REPLACE_EXISTING);
            Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Logger.error(e);
        }

        for (int idx = 0; idx < removedLines.size(); idx++) {
            System.out.print(String.format("Removed: %s %s%n", todoIdxs.get(idx), removedLines.get(idx)));
        }
        eventBus.post(new RemoveEvent());
    }
}
