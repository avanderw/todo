package net.avdw.todo.admin;

import com.google.inject.Inject;
import net.avdw.todo.RunningStats;
import net.avdw.todo.Todo;
import net.avdw.todo.file.TodoFile;
import net.avdw.todo.file.TodoFileFactory;
import net.avdw.todo.template.TemplateExecutor;
import net.avdw.todo.template.TemplateViewModel;
import org.pmw.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Command(name = "backup", description = "Write todo.txt.bak")
public class TodoBackup implements Runnable {
    @ParentCommand
    private Todo todo;

    @Inject
    private TemplateExecutor templateExecutor;

    @Inject
    private TodoFileFactory todoFileFactory;

    @Inject
    private RunningStats runningStats;

    /**
     * Entry point for picocli.
     */
    @Override
    public void run() {
        backup(todoFileFactory.create(todo.getTodoFile()));
    }

    /**
     * Copy a file from one location to another.
     */
    public void backup(final TodoFile todoFile) {
        runningStats.start();
        try {
            Files.copy(todoFile.getPath(), todoFile.getBackupPath(), StandardCopyOption.REPLACE_EXISTING);
            Logger.debug(String.format("Replaced '%s' with '%s'", todoFile.getPath(), todoFile.getBackupPath()));
        } catch (IOException e) {
            Logger.error(String.format("Error writing '%s'", todoFile.getPath()));
            Logger.debug(e);
        }
        runningStats.finish();

        TemplateViewModel templateViewModel = new TemplateViewModel("todo-backup");
        templateViewModel.setWorkingFile(todoFile);
        System.out.println(templateExecutor.executor(templateViewModel));
    }
}
