package net.avdw.todo.extension;

import net.avdw.todo.domain.Todo;

import java.io.PrintWriter;
import java.util.List;

public interface PostAddon {
    void process(List<Todo> todoList, PrintWriter out);
}
