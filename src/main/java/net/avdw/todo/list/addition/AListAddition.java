package net.avdw.todo.list.addition;

import net.avdw.todo.repository.model.ATask;

public interface AListAddition {
    ATask add(String summary);
}
