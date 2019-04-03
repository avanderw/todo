package net.avdw.todo.done;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DoneFunc {
    private File file;
    private File doneFile;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DoneFunc(File file) {
        this.file = file;
        this.doneFile = file.toPath().subpath(0, file.toPath().getNameCount()-1).resolve("done.txt").toFile();
    }

    public void done(Integer idx) {
        String todoIdx = String.format("[%s]", StringUtils.leftPad(idx.toString(), 2, "0"));
        String removedLine = null;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line =scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    if (idx == count) {
                        removedLine = line;
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }

        removedLine = String.format("x %s %s%n", sdf.format(new Date()), removedLine);
        try {
            Files.copy(file.toPath(), Paths.get(file.toString()+".bak"));
            Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(doneFile.toPath(), removedLine.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.error(e);
        }

        System.out.print(String.format("Done: %s %s", todoIdx, removedLine));
    }
}
