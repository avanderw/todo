package net.avdw.todo.core;

import net.avdw.todo.domain.Todo;

import java.util.List;

public interface Ext<T> extends Guard<Todo> {
    List<T> getValueList(final Todo todo);
}
