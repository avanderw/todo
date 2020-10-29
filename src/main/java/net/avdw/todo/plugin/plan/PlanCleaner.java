package net.avdw.todo.plugin.plan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class PlanCleaner {
    private final PlanExt planExt;

    @Inject
    PlanCleaner(final PlanExt planExt) {
        this.planExt = planExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (String ext : planExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
