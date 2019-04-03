package net.avdw.todo.cli;

import net.avdw.todo.list.ListFunc;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddFunc {

    private File file;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public AddFunc(File file) {
        this.file = file;
    }

    public void add(String todoItem) {
        String add = String.format("%s %s", sdf.format(new Date()), todoItem);
        try {
            Files.write(file.toPath(), add.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.error(e);
        }

        int count = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                if (!scanner.nextLine().isEmpty()) {
                    count++;
                }
            }
        } catch (FileNotFoundException e) {
            Logger.error(e);
        }
        System.out.println(String.format("Added: %s %s", count, todoItem));
    }
}
