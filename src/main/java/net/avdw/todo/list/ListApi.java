package net.avdw.todo.list;

import java.util.List;

public interface ListApi {
    List<String> list();

    List<String> list(List<String> list);

    List<String> listPriority();

    List<String> listContexts();

    List<String> listProjects();

    List<String> listAll();
}
