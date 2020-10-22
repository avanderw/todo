package net.avdw.todo.core.mixin;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.TodoTextCleaner;
import net.avdw.todo.domain.Todo;
import picocli.CommandLine.Option;

@Singleton
public class CleanMixin {
    @Option(names = "--clean", descriptionKey = "list.clean.desc")
    private boolean isClean = false;

    private TodoTextCleaner todoTextCleaner;

    @Inject
    CleanMixin(final TodoTextCleaner todoTextCleaner) {
        this.todoTextCleaner = todoTextCleaner;
    }

    public String clean(final Todo todo) {
        return isClean ? todoTextCleaner.clean(todo) : todo.getText();
    }
}
