package net.avdw.todo.repository.model;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class ATaskList extends AItem {
    private List<ATask> taskList = new ArrayList<>();

    public List<ATask> getTasks() {
        return ImmutableList.copyOf(taskList);
    }
}
