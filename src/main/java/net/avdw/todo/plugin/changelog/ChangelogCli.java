package net.avdw.todo.plugin.changelog;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.TodoTextCleaner;
import net.avdw.todo.domain.IsAdded;
import net.avdw.todo.domain.IsContaining;
import net.avdw.todo.domain.IsDone;
import net.avdw.todo.domain.IsParked;
import net.avdw.todo.domain.IsRemoved;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.core.mixin.BooleanFilterMixin;
import net.avdw.todo.core.mixin.DateFilterMixin;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Command(name = "changelog", resourceBundle = "messages", description = "${bundle:changelog}")
public class ChangelogCli implements Runnable, IExitCodeGenerator {
    private final SimpleDateFormat collectMonthlyFormat = new SimpleDateFormat("MMMMM yyyy");
    private final SimpleDateFormat collectYearlyFormat = new SimpleDateFormat("yyyy");
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Mixin
    private BooleanFilterMixin booleanFilter;
    @Mixin
    private DateFilterMixin dateFilter;
    @ArgGroup
    private Exclusive exclusive = new Exclusive();
    private int exitCode = 0;
    @Option(names = "--clean", descriptionKey = "changelog.clean.desc")
    private boolean isClean = false;
    @Inject
    private Repository<Integer, Todo> repository;
    @Spec
    private CommandSpec spec;
    @Inject
    private StyleApplicator styleApplicator;
    @Inject
    private TemplatedResource templatedResource;
    private final Function<Date, String> collectWeekly = date -> {
        String title;
        if (date == null) {
            title = templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_NA);
        } else {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            title = String.format("%s/%02d", collectYearlyFormat.format(date), calendar.get(Calendar.WEEK_OF_YEAR));
        }
        return templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_HEADER,
                String.format("{date:'%s'}", title));
    };
    private final Function<Date, String> collectYearly = date -> {
        String title = date == null
                ? templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_NA)
                : collectYearlyFormat.format(date);

        return templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_HEADER,
                String.format("{date:'%s'}", title));
    };
    private final Function<Date, String> collectMonthly = date -> {
        String title = date == null
                ? templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_NA)
                : collectMonthlyFormat.format(date);
        return templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_HEADER,
                String.format("{date:'%s'}", title));
    };
    @Inject
    private TodoTextCleaner todoTextCleaner;

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void run() {
        try {
            Specification<Integer, Todo> specification = dateFilter.specification();
            specification = specification.and(booleanFilter.specification());

            Logger.debug(specification);
            Function<Date, String> groupingBy = collectMonthly;
            if (exclusive.byWeek) {
                groupingBy = collectWeekly;
            } else if (exclusive.byYear) {
                groupingBy = collectYearly;
            }
            Function<Date, String> finalGroupingBy = groupingBy;

            Map<String, Map<String, List<Todo>>> byPeriodByTypeTodoListMap = new HashMap<>();
            updateChangelog(specification.and(new IsAdded()),
                    (t) -> finalGroupingBy.apply(t.getAdditionDate()),
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_ADDED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsDone()),
                    (t) -> finalGroupingBy.apply(t.getDoneDate()),
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DONE_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsRemoved()),
                    (t) -> finalGroupingBy.apply(t.getRemovedDate()),
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_REMOVED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsParked()),
                    (t) -> finalGroupingBy.apply(t.getParkedDate()),
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_PARKED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsContaining("started:")),
                    (t) -> {
                        String grouping = templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_NA);
                        try {
                            grouping = finalGroupingBy.apply(isoFormat.parse(t.getTagValueList("started").get(0)));
                        } catch (ParseException e) {
                            Logger.debug(e);
                        }
                        return grouping;
                    },
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_STARTED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.not(new IsAdded()).not(new IsDone()).not(new IsRemoved()).not(new IsParked()),
                    (t) -> templatedResource.populateKey(ResourceBundleKey.CHANGELOG_DATE_NA),
                    templatedResource.populateKey(ResourceBundleKey.CHANGELOG_TYPE_NA),
                    byPeriodByTypeTodoListMap);

            if (byPeriodByTypeTodoListMap.isEmpty()) {
                spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.NO_TODO_FOUND));
            }

            byPeriodByTypeTodoListMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(periodByTypeTodoListMap -> {
                spec.commandLine().getOut().println(String.format("%n%s", periodByTypeTodoListMap.getKey()));
                periodByTypeTodoListMap.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(periodTypeTodoListMap -> {
                    spec.commandLine().getOut().println(periodTypeTodoListMap.getKey());
                    periodTypeTodoListMap.getValue().forEach(todo -> {
                        String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
                        spec.commandLine().getOut().println(templatedResource.populateKey(ResourceBundleKey.TODO_LINE_ITEM,
                                String.format("{idx:'%3s',todo:\"%s\"}", todo.getIdx(), styleApplicator.apply(todoText).replaceAll("\"", "\\\\\""))));
                    });
                });
            });
        } catch (UnsupportedOperationException e) {
            Logger.debug(e);
            exitCode = 1;
        }
    }

    private void updateChangelog(final Specification<Integer, Todo> specification, final Function<Todo, String> groupingBy, final String type, final Map<String, Map<String, List<Todo>>> byPeriodByTypeTodoListMap) {
        List<Todo> todoList = repository.findAll(specification);
        Map<String, List<Todo>> byPeriodTodoListMap = todoList.stream().collect(Collectors.groupingBy(groupingBy));
        byPeriodTodoListMap.forEach((key, value) -> {
            byPeriodByTypeTodoListMap.putIfAbsent(key, new HashMap<>());
            Map<String, List<Todo>> periodByTypeTodoListMap = byPeriodByTypeTodoListMap.get(key);
            periodByTypeTodoListMap.putIfAbsent(type, new ArrayList<>());
            List<Todo> periodTypeTodoList = periodByTypeTodoListMap.get(type);
            periodTypeTodoList.addAll(value);
        });
    }

    private static class Exclusive {
        @Option(names = "--by-week", descriptionKey = "changelog.by.month.desc")
        private boolean byWeek = false;
        @Option(names = "--by-year", descriptionKey = "changelog.by.year.desc")
        private boolean byYear = false;
    }
}
