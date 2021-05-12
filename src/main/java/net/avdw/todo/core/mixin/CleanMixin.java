package net.avdw.todo.core.mixin;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoTextCleaner;
import picocli.CommandLine.Option;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CleanMixin {
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;

    private final TodoTextCleaner todoTextCleaner;

    @Inject
    public CleanMixin(final TodoTextCleaner todoTextCleaner) {
        this.todoTextCleaner = todoTextCleaner;
    }

    public String clean(final Todo todo) {
        return isClean ? todoTextCleaner.clean(todo) : todo.getText();
    }
}
