package net.avdw.todo.repository.file;

import com.google.inject.Inject;
import com.google.inject.Provider;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.model.ATask;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class FileTaskRepository implements ARepository<ATask> {
    private Path repositoryPath;
    private Provider<ATask> aTaskProvider;

    @Inject
    public FileTaskRepository(Provider<ATask> aTaskProvider) {
        this.aTaskProvider = aTaskProvider;
    }

    @Inject
    public void setRepositoryPath(@FileTask Path repositoryPath) {
        this.repositoryPath = repositoryPath;
        Logger.debug("Repository set to {}\\", repositoryPath);
    }

    @Override
    public void init() {
        if (!Files.exists(repositoryPath.resolve(".todo"))) {
            File file = repositoryPath.resolve(".todo/todo.txt").toFile();
            try {
                if (file.getParentFile().mkdirs() && file.createNewFile()) {
                    Logger.debug(String.format("%s created", file));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        } else {
            Logger.warn("Repository {}/ already exists!", repositoryPath);
        }
    }

    @Override
    public void add(ATask task) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask retrieve(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask update(ATask aTaskList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ATask delete(ATask aTaskList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ATask> list() {
        Path todoPath = resolveTodoPath();
        List<ATask> list = new ArrayList<>();
        try (Scanner scanner = new Scanner(todoPath)) {
            while (scanner.hasNext()) {
                String lineItem = scanner.nextLine();
                if (lineItem.isEmpty()) {
                    continue;
                }

                ATask aTask = aTaskProvider.get();
                aTask.setSummary(lineItem);
                list.add(aTask);

//                lineItem = String.format("[%s] %s", StringUtils.leftPad(Integer.toString(count), 2, "0"), lineItem);
//                if (filters.isEmpty()) {
//                    list.add(lineItem);
//                } else if (filters.stream().allMatch(lineItem::contains)) {
//                    list.add(lineItem);
//                }
            }
        } catch (IOException e) {
            Logger.error(e);
        }

        return list;
    }

    @Override
    public List<ATask> list(Predicate<ATask> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveList(List<ATask> list) {
        throw new UnsupportedOperationException();
    }

    private Path resolveTodoPath() {
        if (!Files.exists(repositoryPath.resolve(".todo"))) {
            Logger.warn("File repository {}\\ does not exist", repositoryPath);
            throw new UnsupportedOperationException();
        }

        if (!Files.exists(repositoryPath.resolve(".todo/todo.txt"))) {
            throw new UnsupportedOperationException();
        }

        return repositoryPath.resolve(".todo/todo.txt");
    }
}
