package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import lombok.SneakyThrows;
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
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private Path todoPath;

    @Override
    @SneakyThrows
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {
        String todayFormat = simpleDateFormat.format(new Date());
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
