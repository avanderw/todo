package net.avdw.todo.done;

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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DoneTodoTxt implements DoneApi {
    private final File todoFile;
    private final File doneFile;
    private final SimpleDateFormat sdf;

    @Inject
    public DoneTodoTxt(File todoFile, SimpleDateFormat sdf) {
        this.todoFile = todoFile;
        this.doneFile = new File(todoFile.toString().substring(0, todoFile.toString().lastIndexOf("\\")+1) + "done.txt");
        this.sdf = sdf;
    }

    public void done(Integer idx) {
        done(Collections.singletonList(idx));
    }
    public void done(List<Integer> idxs) {
        done(new DoneEvent(idxs));
    }

    @Subscribe
    void done(DoneEvent doneEvent) {
        List<String> todoIdxs = doneEvent.idxs.stream().map(idx->String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"))).collect(Collectors.toList());
        List<String> removedLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(todoFile)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    int finalCount = count;
                    if (doneEvent.idxs.stream().anyMatch(idx -> idx == finalCount)) {
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

        removedLines = removedLines.stream().map(removedLine -> String.format("x %s %s%n", sdf.format(new Date()), removedLine)).collect(Collectors.toList());
        try {
            Files.copy(todoFile.toPath(), Paths.get(todoFile.toString() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            Files.write(todoFile.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(doneFile.toPath(), removedLines.stream().reduce("", (id, line) -> id + line).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.error(e);
        }

        for (int idx = 0; idx < removedLines.size(); idx++) {
            System.out.print(String.format("Done: %s %s", todoIdxs.get(idx), removedLines.get(idx)));
        }
    }
}
