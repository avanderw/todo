package net.avdw.todo.list.filtering;

import java.util.List;

public interface AFilter {
    List<String> list();

    List<String> list(List<String> filters);
}
