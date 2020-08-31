package net.avdw.todo;

public final class ResourceBundleKey {
    public static final String INIT_FILE_EXISTS = "init.file.exists(path)";
    public static final String INIT_FILE_CREATED = "init.file.created(path,usage)";
    public static final String TODO_LINE_ITEM = "todo.line.item(idx,todo)";
    public static final String NO_PRIORITY_TODO = "no.priority.todo";
    public static final String PRIORITY_NOT_ALLOWED_DONE = "priority.not.allowed.done";
    public static final String PRIORITY_NOT_ALLOWED_REMOVED = "priority.not.allowed.removed";
    public static final String PRIORITY_NOT_ALLOWED_PARKED = "priority.not.allowed.parked";

    private ResourceBundleKey() {
    }
}
