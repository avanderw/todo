package net.avdw.todo.priority;

import java.io.File;

public class PriorityFunc {
    private final File todoFile;

    public PriorityFunc(File todoFile) {

        this.todoFile = todoFile;
    }

    public void add(Integer idx, String priority) {
        throw new UnsupportedOperationException();
    }

    public void remove(Integer idx) {
        throw new UnsupportedOperationException();
    }
}
