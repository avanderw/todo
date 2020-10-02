package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.*;
import picocli.CommandLine.Model.CommandSpec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Command(name = "changelog", resourceBundle = "messages", description = "${bundle:changelog}")
public class ChangelogCli implements Runnable, IExitCodeGenerator {
    private final SimpleDateFormat collectMonthlyFormat = new SimpleDateFormat("yyyy/MM");
    private final SimpleDateFormat collectYearlyFormat = new SimpleDateFormat("yyyy");
    private final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Mixin
    private BooleanFilter booleanFilter;
    @Mixin
    private DateFilter dateFilter;
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
    private TemplatedResourceBundle templatedResourceBundle;
    private final Function<Date, String> collectWeekly = date -> {
        String title;
        if (date == null) {
            title = templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_NA);
        } else {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            title = String.format("%s/%02d", collectYearlyFormat.format(date), calendar.get(Calendar.WEEK_OF_YEAR));
        }
        return templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_HEADER,
                String.format("{date:'%s'}", title));
    };
    private final Function<Date, String> collectYearly = date -> {
        String title = date == null
                ? templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_NA)
                : collectYearlyFormat.format(date);

        return templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_HEADER,
                String.format("{date:'%s'}", title));
    };
    private final Function<Date, String> collectMonthly = date -> {
        String title = date == null
                ? templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_NA)
                : collectMonthlyFormat.format(date);
        return templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_HEADER,
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
            Specification<Integer, Todo> specification = new Any<>();
            specification = dateFilter.specification(specification);
            specification = booleanFilter.specification(specification);
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
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_ADDED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsDone()),
                    (t) -> finalGroupingBy.apply(t.getDoneDate()),
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DONE_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsRemoved()),
                    (t) -> finalGroupingBy.apply(t.getRemovedDate()),
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_REMOVED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsParked()),
                    (t) -> finalGroupingBy.apply(t.getParkedDate()),
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_PARKED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.and(new IsContaining("started:")),
                    (t) -> {
                        String grouping = templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_NA);
                        try {
                            grouping = finalGroupingBy.apply(isoFormat.parse(t.getTagValueList("started").get(0)));
                        } catch (ParseException e) {
                            Logger.debug(e);
                        }
                        return grouping;
                    },
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_STARTED_HEADER),
                    byPeriodByTypeTodoListMap);
            updateChangelog(specification.not(new IsAdded()).not(new IsDone()).not(new IsRemoved()).not(new IsParked()),
                    (t) -> templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_DATE_NA),
                    templatedResourceBundle.getString(ResourceBundleKey.CHANGELOG_TYPE_NA),
                    byPeriodByTypeTodoListMap);

            if (byPeriodByTypeTodoListMap.isEmpty()) {
                spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_TODO_FOUND));
            }

            byPeriodByTypeTodoListMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(periodByTypeTodoListMap -> {
                spec.commandLine().getOut().println(String.format("%n%s", periodByTypeTodoListMap.getKey()));
                periodByTypeTodoListMap.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(periodTypeTodoListMap -> {
                    spec.commandLine().getOut().println(periodTypeTodoListMap.getKey());
                    periodTypeTodoListMap.getValue().forEach(todo -> {
                        String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
                        spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
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