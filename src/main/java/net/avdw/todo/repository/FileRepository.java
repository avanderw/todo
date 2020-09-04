package net.avdw.todo.repository;

import lombok.SneakyThrows;
import org.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileRepository<T extends IdType<Integer>> implements Repository<Integer, T> {
    private final List<T> itemList = new ArrayList<>();
    private final Path path;
    private boolean autoCommit = true;

    @SneakyThrows
    public FileRepository(final Path path, final FileTypeBuilder<T> builder) {
        this.path = path;
        if (Files.exists(path)) {
            List<String> readAllLines = Files.readAllLines(path);
            for (int i = 0; i < readAllLines.size(); i++) {
                String line = readAllLines.get(i);
                itemList.add(builder.build(i, line));
            }
        } else {
            Logger.debug("Path does not exist {}", path.toUri());
        }
    }

    @Override
    public void add(final T item) {
        item.setId(itemList.size());
        itemList.add(item);
        if (autoCommit) {
            commit();
        }
    }

    @Override
    public void addAll(final List<T> addItemList) {
        addItemList.forEach(item -> {
            item.setId(itemList.size());
            itemList.add(item);
        });

        if (autoCommit) {
            commit();
        }
    }

    @SneakyThrows
    @Override
    public void commit() {
        Logger.debug("Writing {}", path);
        Files.write(path, itemList.stream().map(Object::toString).collect(Collectors.toList()));
    }

    @Override
    public List<T> findAll(final Specification<Integer, T> specification) {
        return itemList.stream().filter(specification::isSatisfiedBy).collect(Collectors.toList());
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
    public void removeAll(final Specification<Integer, T> specification) {
        itemList.removeAll(findAll(specification));
        if (autoCommit) {
            commit();
        }
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Override
    public int size() {
        return itemList.size();
    }

    @Override
    public void update(final T item) {
        if (item.getId() < itemList.size()) {
            itemList.set(item.getId(), item);
            if (autoCommit) {
                commit();
            }
        } else {
            Logger.debug("Line ({}) not found in repository", item.getId());
        }
    }
}
