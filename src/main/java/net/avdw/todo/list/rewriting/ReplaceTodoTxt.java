package net.avdw.todo.list.rewriting;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.list.addition.AddTodoTxt;
import net.avdw.todo.list.removal.RemoveTodoTxt;

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
