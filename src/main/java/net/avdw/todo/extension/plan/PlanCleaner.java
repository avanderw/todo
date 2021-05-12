package net.avdw.todo.extension.plan;

import net.avdw.todo.domain.Todo;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlanCleaner {
    private final PlanTodoTxtExt planExt;

    @Inject
    PlanCleaner(final PlanTodoTxtExt planExt) {
        this.planExt = planExt;
    }

    public String clean(final Todo todo) {
        String text = todo.getText();
        for (final String ext : planExt.getSupportedExtList()) {
            text = text.replaceAll(String.format("\\s%s:\\S+", ext), "");
        }
        return text;
    }
}
