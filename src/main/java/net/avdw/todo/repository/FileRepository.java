package net.avdw.todo.repository;

import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileRepository<T> implements Repository<T> {
    private final Path path;
    private final List<T> itemList = new ArrayList<>();
    private boolean autoCommit = true;

    @SneakyThrows
    public FileRepository(final Path path, final TypeBuilder<T> builder) {
        this.path = path;
        if (Files.exists(path)) {
            Files.readAllLines(path).forEach(line -> itemList.add(builder.build(line)));
        } else {
            Logger.debug("Path does not exist {}", path.toUri());
        }
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public List<T> findAll(final Specification<T> specification) {
        return itemList.stream().filter(specification::isSatisfiedBy).collect(Collectors.toList());
    }

    @Override
    public void add(final T item) {
        itemList.add(item);
        if (autoCommit) {
            commit();
        }
    }

    @Override
    public T findById(final int id) {
        if (id < itemList.size()) {
            return itemList.get(id);
        }

        Logger.debug("Line ({}) not found in repository", id);
        return null;
    }

    @Override
    public void save(final int id, final T item) {
        if (id < itemList.size()) {
            itemList.set(id, item);
            if (autoCommit) {
                commit();
            }
        } else {
            Logger.debug("Line ({}) not found in repository", id);
        }
    }

    @SneakyThrows
    @Override
    public void commit() {
        Logger.debug("Writing {}", path);
        Files.write(path, itemList.stream().map(Object::toString).collect(Collectors.toList()));
    }

    @Override
    public void addAll(final List<T> addItemList) {
        itemList.addAll(addItemList);
        if (autoCommit) {
            commit();
        }
    }

    @Override
    public void removeAll(final Specification<T> specification) {
        itemList.removeAll(findAll(specification));
        if (autoCommit) {
            commit();
        }
    }

    @Override
    public List<T> findAll() {
        return itemList;
    }
}
