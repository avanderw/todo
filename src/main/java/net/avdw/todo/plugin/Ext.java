package net.avdw.todo.plugin;

import net.avdw.todo.Guard;
import net.avdw.todo.domain.Todo;

import java.util.List;
import java.util.Optional;

public interface Ext<T> extends Guard<Todo> {
    List<String> getSupportedExtList();

    Optional<T> getValue(final Todo todo);

    List<T> getValueList(final Todo todo);

    String preferredExt();
}
