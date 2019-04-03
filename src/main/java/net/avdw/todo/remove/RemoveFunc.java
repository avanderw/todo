package net.avdw.todo.remove;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class RemoveFunc {
    private File file;

    public RemoveFunc(File file) {
        this.file = file;
    }

    public void remove(Integer idx) {
        String todoItem = String.format("[%s] ", StringUtils.leftPad(idx.toString(), 2, "0"));
        StringBuilder sb = new StringBuilder();
        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line =scanner.nextLine();
                if (!line.isEmpty()) {
                    count++;
                    if (idx == count) {
                        todoItem = todoItem + line;
                    } else {
                        sb.append(line).append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }

        try {
            Files.copy(file.toPath(), Paths.get(file.toString()+".bak"));
            Files.write(file.toPath(), sb.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Logger.error(e);
        }

        System.out.println(String.format("Removed: %s", todoItem));
    }
}
