package net.avdw.todo.style.painter;

import net.avdw.todo.Guard;
import net.avdw.todo.domain.Todo;

public interface IDefaultPainter extends IPainter, Guard<Todo> {
    boolean isFallback();
    String color();
}
