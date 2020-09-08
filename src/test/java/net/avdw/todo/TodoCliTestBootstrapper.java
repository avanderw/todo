package net.avdw.todo;

import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TodoCliTestBootstrapper {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String todayDate = SIMPLE_DATE_FORMAT.format(new Date());

    private TodoCliTestBootstrapper() {
    }

    @SneakyThrows
    public static void cleanup(final Path todoPath) {
        Files.deleteIfExists(todoPath);
        Files.deleteIfExists(todoPath.getParent().resolve("todo.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("done.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("done.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("parked.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("parked.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent().resolve("removed.txt"));
        Files.deleteIfExists(todoPath.getParent().resolve("removed.txt_" + todayDate));
        Files.deleteIfExists(todoPath.getParent());
        Files.deleteIfExists(todoPath.getParent().getParent());
    }

    @SneakyThrows
    public static void setup(final Path todoPath) {
        Files.createDirectories(todoPath.getParent());
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void warmup(final CliTester cliTester) {
        cliTester.execute();
        cliTester.execute("--help");
        cliTester.execute("--version");
    }
}
