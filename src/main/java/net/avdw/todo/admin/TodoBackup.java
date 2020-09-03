package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.MainCli;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 * @see net.avdw.todo.BackupCli
 */
@Deprecated
@Command(name = "backup", description = "Write todo.txt.bak")
public class TodoBackup implements Runnable {
    @ParentCommand
    private MainCli mainCli;

    @Inject
    private TemplateExecutor templateExecutor;
    @Inject
    private TodoFileFactory todoFileFactory;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        TodoFile fileBefore = todoFileFactory.create(mainCli.getTodoFile());

        try {
            Files.copy(fileBefore.getPath(), fileBefore.getBackupPath(), StandardCopyOption.REPLACE_EXISTING);
            Logger.debug(String.format("Replaced '%s' with '%s'", fileBefore.getBackupPath(), fileBefore.getPath()));
        } catch (IOException e) {
            Logger.error(String.format("Error writing '%s'", fileBefore.getBackupPath()));
            Logger.debug(e);
        }

        TemplateViewModel templateViewModel = new TemplateViewModel("backup", new ArrayList<>(), fileBefore, todoFileFactory.create(fileBefore.getBackupPath()));
        System.out.println(templateExecutor.executor(templateViewModel));
    }

}
