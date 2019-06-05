package net.avdw.todo.list.removal;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RemoveTodoTxt implements RemoveApi {
    private File file;

    @Inject
    public RemoveTodoTxt(File file) {
        this.file = file;
    }

    @Subscribe
    public void remove(RemoveEvent removeEvent) {
        List<String> todoIdxs = removeEvent.idxs.stream().map(idx->String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"))).collect(Collectors.toList());
        List<String> removedLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line =scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    int finalCount = count;
                    if (removeEvent.idxs.stream().anyMatch(idx->idx == finalCount)) {
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
    }

    @Override
    public void remove(Integer idx) {
        remove(new RemoveEvent(Collections.singletonList(idx)));
    }

    @Override
    public void remove(List<Integer> idxs) {
        remove(new RemoveEvent(idxs));
    }
}
