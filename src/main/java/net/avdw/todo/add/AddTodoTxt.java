package net.avdw.todo.add;

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
import java.util.Date;
import java.util.Scanner;

public class AddTodoTxt implements AddApi {
    private File file;
    private SimpleDateFormat sdf;

    @Inject
    public AddTodoTxt(File file, SimpleDateFormat sdf) {
        this.file = file;
        this.sdf = sdf;
    }

    @Subscribe
    public void add(AddEvent event) {
        if (event.todo.isEmpty()) {
            Logger.warn("There is no todo item to add.");
            return;
        }

        String add = String.format("%s %s%n", sdf.format(new Date()), event.todo);
        try {
            if (file.exists()) {
                Files.copy(file.toPath(), Paths.get(file.toString() + ".bak"), StandardCopyOption.REPLACE_EXISTING);
            }
            Files.write(file.toPath(), add.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
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
        System.out.print(String.format("Added: [%s] %s%n", StringUtils.leftPad(Integer.toString(count), 2, "0"), event.todo));
    }

    @Override
    public void add(String todo) {
        add(new AddEvent(todo));
    }
}
