package net.avdw.todo.replace;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.add.AddTodoTxt;
import net.avdw.todo.remove.RemoveTodoTxt;

public class ReplaceTodoTxt implements ReplaceApi {
    private final AddTodoTxt addTodoTxt;
    private final RemoveTodoTxt removeTodoTxt;

    @Inject
    public ReplaceTodoTxt(AddTodoTxt addTodoTxt, RemoveTodoTxt removeTodoTxt) {
        this.addTodoTxt = addTodoTxt;
        this.removeTodoTxt = removeTodoTxt;
    }

    @Subscribe
    public void replace(ReplaceEvent replaceEvent) {
        removeTodoTxt.remove(replaceEvent.idx);
        addTodoTxt.add(replaceEvent.todoItem);
    }

    @Override
    public void replace(Integer idx, String todoItem) {
        replace(new ReplaceEvent(idx, todoItem));
    }
}
