package net.avdw.todo.add;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        if (todoItem.isEmpty()) {
            Logger.warn("There is no todo item to add");
            return;
        }

        String add = String.format("%s %s%n", sdf.format(new Date()), todoItem);
        try {
            Files.copy(file.toPath(), Paths.get(file.toString()+".bak"));
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
        System.out.print(String.format("Added: %s %s", count, todoItem));
    }
}
