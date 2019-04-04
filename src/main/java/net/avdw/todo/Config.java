package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final File TODO_DIR = new File(String.format("%s/.todo", System.getProperty("user.home")));
    private static final File DONE_FILE = new File(String.format("%s/.todo/done.txt", System.getProperty("user.home")));
    public static final File TODO_FILE = new File(String.format("%s/.todo/todo.txt", System.getProperty("user.home")));

    static {
        createNewFile(TODO_FILE);
        createNewFile(DONE_FILE);
    }

    private static void createNewFile(File file) {
        if (!file.exists()) {
            if (!TODO_DIR.exists() && !TODO_DIR.mkdirs()){
                Logger.warn(String.format("Could not create directories %s", TODO_DIR));
            }
            try {
                if (!file.createNewFile()) {
                    Logger.warn(String.format("Could not create file %s", file));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }
}
