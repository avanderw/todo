package net.avdw.todo;

import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;

public class Config {
    public static final File TODO_DIR = new File(String.format("%s/.todo", System.getProperty("user.home")));
    public static final File TODO_FILE = new File(String.format("%s/.todo/todo.txt", System.getProperty("user.home")));

    static {
        if (!TODO_FILE.exists()) {
            if (!TODO_DIR.mkdirs()){
                Logger.warn(String.format("Could not create directories %s", TODO_FILE));
            }
            try {
                if (!TODO_FILE.createNewFile()) {
                    Logger.warn(String.format("Could not create file %s", TODO_FILE));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
    }
}
