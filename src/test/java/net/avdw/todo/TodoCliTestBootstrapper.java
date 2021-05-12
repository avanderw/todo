package net.avdw.todo;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class TodoCliTestBootstrapper {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String todayDate = SIMPLE_DATE_FORMAT.format(new Date());

    private TodoCliTestBootstrapper() {
    }

    @SneakyThrows
    public static void cleanup(final Path todoPath) {
        Files.walkFileTree(todoPath.getParent().getParent(),
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(
                            Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
                });
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
        cliTester.execute("ls");
    }
}
