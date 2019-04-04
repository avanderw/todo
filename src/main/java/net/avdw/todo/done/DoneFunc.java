package net.avdw.todo.done;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DoneFunc {
    private final File todoFile;
    private final File doneFile;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DoneFunc(File todoFile) {
        this.todoFile = todoFile;
        this.doneFile = todoFile.toPath().subpath(0, todoFile.toPath().getNameCount() - 1).resolve("done.txt").toFile();
    }

    public void done(Integer idx) {
        done(Collections.singletonList(idx));
    }
    public void done(List<Integer> idxs) {
        List<String> todoIdxs = idxs.stream().map(idx->String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"))).collect(Collectors.toList());
        List<String> removedLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    int finalCount = count;
                    if (idxs.stream().anyMatch(idx -> idx == finalCount)) {
                        removedLines.add(line);
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }

        removedLines = removedLines.stream().map(removedLine -> String.format("x %s %s%n", sdf.format(new Date()), removedLine)).collect(Collectors.toList());
        try {
            Files.copy(todoFile.toPath(), Paths.get(todoFile.toString() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            Files.write(todoFile.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(doneFile.toPath(), removedLines.stream().reduce("", (id, line) -> id + line).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.error(e);
        }

        for (int idx = 0; idx < todoIdxs.size(); idx++) {
            System.out.print(String.format("Done: %s %s", todoIdxs.get(idx), removedLines.get(idx)));
        }
    }
}
