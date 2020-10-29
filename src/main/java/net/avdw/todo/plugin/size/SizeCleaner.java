package net.avdw.todo.plugin.size;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class SizeCleaner {
    private final SizeExt sizeExt;

    @Inject
    SizeCleaner(final SizeExt sizeExt) {
        this.sizeExt = sizeExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (String ext : sizeExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
