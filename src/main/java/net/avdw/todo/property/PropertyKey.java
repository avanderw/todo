package net.avdw.todo.property;

import net.avdw.todo.Main;
import net.avdw.todo.action.TodoAdd;
import net.avdw.todo.admin.TodoEdit;
import net.avdw.todo.config.LoggingSetup;

public final class PropertyKey {
    public static final String TODO_ADD_AUTO_DATE = String.format("%s.date", TodoAdd.class.getCanonicalName());
    public static final String LOGGING_LEVEL = String.format("%s.level", LoggingSetup.class.getCanonicalName());
    public static final String EDITOR_PATH = String.format("%s.editor", TodoEdit.class.getCanonicalName());
    public static final String RELEASE_MODE = String.format("%s.release", Main.class.getCanonicalName());

    private PropertyKey() {
    }
}
