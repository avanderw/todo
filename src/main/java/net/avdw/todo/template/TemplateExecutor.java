package net.avdw.todo.template;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.google.inject.Inject;
import net.avdw.todo.RunningStats;
import net.avdw.todo.theme.ThemeApplicator;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class TemplateExecutor {
    private final ThemeApplicator themeApplicator;
    private final RunningStats runningStats;

    @Inject
    TemplateExecutor(final ThemeApplicator themeApplicator, final RunningStats runningStats) {
        this.themeApplicator = themeApplicator;
        this.runningStats = runningStats;
    }

    public String executor(final TemplateViewModel templateViewModel) {
        Map<String, Object> context = new HashMap<>();
        context.put("theme", themeApplicator);
        context.put("model", templateViewModel);
        context.put("stats", runningStats);

        Mustache m = new DefaultMustacheFactory().compile(String.format("%s.mustache", templateViewModel.getView()));
        StringWriter writer = new StringWriter();
        m.execute(writer, context);
        return themeApplicator.txt(writer.toString());
    }
}
