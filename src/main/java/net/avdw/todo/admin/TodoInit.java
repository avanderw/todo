package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.Global;
import net.avdw.todo.LocalTodo;
import net.avdw.todo.MainCli;
import net.avdw.todo.theme.ThemeApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @see net.avdw.todo.InitCli
 */
@Deprecated
@Command(name = "init", description = "Initialize .todo directory")
public class TodoInit implements Runnable {
    @ParentCommand
    private MainCli mainCli;

    @Inject
    @Global
    private Path globalPath;

    @Inject
    @LocalTodo
    private Path localPath;
    @Inject
    private ThemeApplicator themeApplicator;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        System.out.println(themeApplicator.header("todo:init"));
        Path path = mainCli.isGlobal() ? globalPath : localPath;

        if (Files.exists(path)) {
            Logger.warn("Directory {} already exists", path.toAbsolutePath());
        } else {
            try {
                Files.createDirectories(path);
                Files.createFile(path.resolve("todo.txt"));
                Logger.info(String.format("Initialized `%s` with a blank todo.txt", path));
            } catch (IOException e) {
                Logger.error(String.format("Could not initialize directory `%s`", path));
                Logger.debug(e);
            }
        }
    }
}
