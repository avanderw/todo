package net.avdw.todo.replace;

import net.avdw.todo.add.AddFunc;
import net.avdw.todo.remove.RemoveFunc;

import java.io.File;

public class ReplaceFunc {
    private File todoFile;

    public ReplaceFunc(File todoFile) {
        this.todoFile = todoFile;
    }

    public void replace(Integer idx, String todoItem) {
        new RemoveFunc(todoFile).remove(idx);
        new AddFunc(todoFile).add(todoItem);
    }
}
