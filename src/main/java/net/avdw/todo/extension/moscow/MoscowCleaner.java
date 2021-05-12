package net.avdw.todo.extension.moscow;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MoscowCleaner {
    private final MoscowTodoTxtExt moscowExt;

    @Inject
    MoscowCleaner(final MoscowTodoTxtExt moscowExt) {
        this.moscowExt = moscowExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (final String ext : moscowExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
