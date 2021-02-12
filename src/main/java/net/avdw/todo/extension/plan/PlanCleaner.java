package net.avdw.todo.extension.plan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import net.avdw.todo.domain.Todo;

@Singleton
public class PlanCleaner {
    private final PlanTodoTxtExt planExt;

    @Inject
    PlanCleaner(final PlanTodoTxtExt planExt) {
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
