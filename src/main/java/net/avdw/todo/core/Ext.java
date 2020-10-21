package net.avdw.todo.core;

import net.avdw.todo.domain.Todo;

import java.util.List;

public interface Ext<T> {
    List<T> getValueList(final Todo todo);
}
