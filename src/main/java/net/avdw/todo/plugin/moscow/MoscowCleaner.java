package net.avdw.todo.plugin.moscow;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class MoscowCleaner {
    private final MoscowExt moscowExt;

    @Inject
    MoscowCleaner(final MoscowExt moscowExt) {
        this.moscowExt = moscowExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (String ext : moscowExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
