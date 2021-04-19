package net.avdw.todo.extension.comment;

import com.google.inject.Inject;
import net.avdw.todo.core.mixin.IndexFilterMixin;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

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
    private static int exitCode = 0;

    @Spec private CommandSpec spec;
    @Mixin private IndexFilterMixin indexSpecificationMixin;
    @Inject private Repository<Integer, Todo> todoRepository;
    @Inject private Path todoPath;

    @Option(names = {"-m", "--message"}, descriptionKey = "message.description")
    private List<String> messageList;


    @Override
    public void run() {
        exitCode = 0;
        Specification<Integer, Todo> specification = new Any<>();
        if (indexSpecificationMixin.isActive()) {
            specification = indexSpecificationMixin;
        } else {
            exitCode = 1;
            spec.commandLine().getErr().println("No todos indexed.");
            return;
        }

        List<Todo> todoList = todoRepository.findAll(specification);
        if (todoList.isEmpty()) {
            exitCode = 1;
            spec.commandLine().getErr().println("No todos found.");
        } else {
            if (messageList == null || messageList.isEmpty()) {
                for (Todo todo : todoList) {
                    List<String> noteIdList = todo.getExtValueList("note");
                    if (noteIdList.isEmpty()) {
                        exitCode = 1;
                        spec.commandLine().getErr().println(todo);
                        spec.commandLine().getErr().println("No note found.");
                    } else {
                        spec.commandLine().getOut().println(todo);
                        for (String noteId : noteIdList) {
                            printNote(noteId);
                        }
                    }
                }
            } else {
                todoRepository.setAutoCommit(false);
                for (Todo todo : todoList) {
                    for (String message : messageList) {
                        addNote(todo, message);
                    }
                    spec.commandLine().getOut().println(todoRepository.findById(todo.getId()).orElseThrow());
                }
                todoRepository.commit();
            }
        }
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private void printNote(String noteId) {
        try {
            Path path = Paths.get(String.format("%s/ext/notes/%s.note", todoPath.getParent().toAbsolutePath(), noteId));
            String content = Files.readString(Paths.get(String.format("%s/ext/notes/%s.note", todoPath.getParent().toAbsolutePath(), noteId)));
            spec.commandLine().getOut().println(String.format("%s: %s", sdf.format(new Date(Files.getLastModifiedTime(path).toMillis())), content));
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private void addNote(Todo todo, String message) {
        todo = todoRepository.findById(todo.getId()).orElseThrow();
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(message.getBytes(StandardCharsets.UTF_8));
            String noteId = String.format("%x", new BigInteger(1, digest.digest())).substring(0, 6);

            Files.createDirectories(Paths.get(String.format("%s/ext/notes/", todoPath.getParent().toAbsolutePath())));
            Files.writeString(Paths.get(String.format("%s/ext/notes/%s.note", todoPath.getParent().toAbsolutePath(), noteId)), message);
            Todo newTodo = new Todo(todo.getId(), String.format("%s note:%s", todo.getText(), noteId));
            todoRepository.update(newTodo);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
