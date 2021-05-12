package net.avdw.todo.extension;

import net.avdw.todo.Guard;
import net.avdw.todo.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface TodoTxtExt<T> extends Guard<Todo> {
    List<String> getSupportedExtList();

    Optional<T> getValue(Todo todo);

    List<T> getValueList(Todo todo);

    String preferredExt();
}
