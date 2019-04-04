package net.avdw.todo.replace;

import com.google.common.eventbus.EventBus;
import net.avdw.todo.add.AddEventDispatcher;
import net.avdw.todo.remove.RemoveFunc;

import java.io.File;

public class ReplaceFunc {
    private File todoFile;
    private EventBus eventBus;

    public ReplaceFunc(File todoFile, EventBus eventBus) {
        this.todoFile = todoFile;
        this.eventBus = eventBus;
    }

    public void replace(Integer idx, String todoItem) {
        new RemoveFunc(todoFile, eventBus).remove(idx);
        new AddEventDispatcher(todoFile, eventBus).add(todoItem);
        eventBus.post(new ReplaceEvent());
    }
}
