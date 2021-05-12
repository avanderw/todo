package net.avdw.todo.extension.comment;

import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Command(name = "comment", aliases = "note", resourceBundle = "comment", description = "${bundle:description}", mixinStandardHelpOptions = true)
public class CommentCli implements Runnable, IExitCodeGenerator {
    private int exitCode = 0;
    private final Repository<Integer, Todo> todoRepository;
    private final Path todoPath;
    @Spec private CommandSpec spec;
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Option(names = {"-m", "--message"}, descriptionKey = "message.description")
    private List<String> messageList;


    @Inject
    CommentCli(final Repository<Integer, Todo> todoRepository, final Path todoPath) {
        this.todoRepository = todoRepository;
        this.todoPath = todoPath;
    }

    @Override
    public void run() {
        exitCode = 0;
        final Specification<Integer, Todo> specification;
        if (indexSpecificationMixin.isActive()) {
            specification = indexSpecificationMixin;
        } else {
            exitCode = 1;
            spec.commandLine().getErr().println("No todos indexed.");
            return;
        }

        final List<Todo> todoList = todoRepository.findAll(specification);
        if (todoList.isEmpty()) {
            exitCode = 1;
            spec.commandLine().getErr().println("No todos found.");
        } else {
            if (messageList == null || messageList.isEmpty()) {
                for (final Todo todo : todoList) {
                    final List<String> noteIdList = todo.getExtValueList("note");
                    if (noteIdList.isEmpty()) {
                        exitCode = 1;
                        spec.commandLine().getErr().println(todo);
                        spec.commandLine().getErr().println("No note found.");
                    } else {
                        spec.commandLine().getOut().println(todo);
                        for (final String noteId : noteIdList) {
                            printNote(noteId);
                        }
                    }
                }
            } else {
                todoRepository.setAutoCommit(false);
                for (final Todo todo : todoList) {
                    for (final String message : messageList) {
                        addNote(todo, message);
                    }
                    spec.commandLine().getOut().println(todoRepository.findById(todo.getId()).orElseThrow());
                }
                todoRepository.commit();
            }
        }
    }

    private void printNote(final String noteId) {
        try {
            final Path parent = todoPath.getParent();
            if (parent == null) {
                throw new UnsupportedOperationException();
            }
            final Path path = Paths.get(String.format("%s/ext/notes/%s.note", parent.toAbsolutePath(), noteId));
            final String content = Files.readString(Paths.get(String.format("%s/ext/notes/%s.note", parent.toAbsolutePath(), noteId)));
            spec.commandLine().getOut().println(String.format("%s: %s", new SimpleDateFormat("yyyy-MM-dd").format(new Date(Files.getLastModifiedTime(path).toMillis())), content));
        } catch (final IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void addNote(final Todo todo, final String message) {
        final Path parent = todoPath.getParent();
        if (parent == null) {
            throw new UnsupportedOperationException();
        }
        final Todo tmpTodo = todoRepository.findById(todo.getId()).orElseThrow();
        try {
            final MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(message.getBytes(StandardCharsets.UTF_8));
            final String noteId = String.format("%x", new BigInteger(1, digest.digest())).substring(0, 6);

            Files.createDirectories(Paths.get(String.format("%s/ext/notes/", parent.toAbsolutePath())));
            Files.writeString(Paths.get(String.format("%s/ext/notes/%s.note", parent.toAbsolutePath(), noteId)), message);
            final Todo newTodo = new Todo(tmpTodo.getId(), String.format("%s note:%s", tmpTodo.getText(), noteId));
            todoRepository.update(newTodo);
        } catch (final NoSuchAlgorithmException | IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
