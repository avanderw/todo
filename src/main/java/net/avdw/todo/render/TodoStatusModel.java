package net.avdw.todo.render;

import java.util.ArrayList;
import java.util.List;

public class TodoStatusModel {
    private List<TodoFileStatusModel> knownPathList = new ArrayList<>();

    public List<TodoFileStatusModel> getKnownPathList() {
        return knownPathList;
    }
}
