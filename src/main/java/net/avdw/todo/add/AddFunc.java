package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddFunc implements AddApi {

    private File file;
    private EventBus eventBus;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public AddFunc(File file, EventBus eventBus) {
        this.file = file;
        this.eventBus = eventBus;
    }

    public void add(String todoItem) {
        if (todoItem.isEmpty()) {
            Logger.warn("There is no todo item to add");
            return;
        }

        String add = String.format("%s %s%n", sdf.format(new Date()), todoItem);
        try {
            Files.copy(file.toPath(), Paths.get(file.toString()+".bak"), StandardCopyOption.REPLACE_EXISTING);
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
        System.out.print(String.format("Added: [%s] %s%n", StringUtils.leftPad(Integer.toString(count), 2, "0"), todoItem));
//        eventBus.post(new AddEvent());
    }
}