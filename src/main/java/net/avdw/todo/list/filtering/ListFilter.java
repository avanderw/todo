package net.avdw.todo.list.filtering;

import java.util.List;

public class ListFilter implements AFilter {
    @Override
    public List<String> list() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> list(List<String> filters) {
        throw new UnsupportedOperationException();
    }
}
