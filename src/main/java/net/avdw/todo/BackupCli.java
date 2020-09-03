package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.SneakyThrows;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Command(name = "backup", resourceBundle = "messages", description = "${bundle:backup}")
public class BackupCli implements Runnable {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private Path todoPath;

    @Override
    @SneakyThrows
    public void run() {
        String todayFormat = SIMPLE_DATE_FORMAT.format(new Date());
        Path todoBackup = todoPath.getParent().resolve(String.format("todo.txt_%s", todayFormat));
        Path donePath = todoPath.getParent().resolve("done.txt");
        Path doneBackup = todoPath.getParent().resolve(String.format("done.txt_%s", todayFormat));
        Path parkedPath = todoPath.getParent().resolve("parked.txt");
        Path parkedBackup = todoPath.getParent().resolve(String.format("parked.txt_%s", todayFormat));
        Path removedPath = todoPath.getParent().resolve("removed.txt");
        Path removedBackup = todoPath.getParent().resolve(String.format("removed.txt_%s", todayFormat));

        Files.copy(todoPath, todoBackup, StandardCopyOption.REPLACE_EXISTING);
        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.BACKUP_COMPLETE,
                gson.fromJson(String.format("{path:'%s'}", todoBackup.toUri()), Map.class)));

        if (Files.exists(donePath)) {
            Files.copy(donePath, doneBackup, StandardCopyOption.REPLACE_EXISTING);
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.BACKUP_COMPLETE,
                    gson.fromJson(String.format("{path:'%s'}", doneBackup.toUri()), Map.class)));
        }
        if (Files.exists(parkedPath)) {
            Files.copy(parkedPath, parkedBackup, StandardCopyOption.REPLACE_EXISTING);
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.BACKUP_COMPLETE,
                    gson.fromJson(String.format("{path:'%s'}", parkedBackup.toUri()), Map.class)));
        }
        if (Files.exists(removedPath)) {
            Files.copy(removedPath, removedBackup, StandardCopyOption.REPLACE_EXISTING);
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.BACKUP_COMPLETE,
                    gson.fromJson(String.format("{path:'%s'}", removedBackup.toUri()), Map.class)));
        }

    }
}
