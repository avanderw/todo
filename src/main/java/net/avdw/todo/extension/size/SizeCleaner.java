package net.avdw.todo.extension.size;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SizeCleaner {
    private final SizeExt sizeExt;

    @Inject
    SizeCleaner(final SizeExt sizeExt) {
        this.sizeExt = sizeExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (final String ext : sizeExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
