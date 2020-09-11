package net.avdw.todo;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.repository.Specification;
import net.avdw.todo.style.StyleApplicator;
import org.tinylog.Logger;
import picocli.CommandLine.IExitCodeGenerator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Command(name = "ls", resourceBundle = "messages", description = "${bundle:ls}")
public class ListCli implements Runnable, IExitCodeGenerator {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Gson gson = new Gson();
    @Option(names = "--added-after", descriptionKey = "ls.after.added.desc")
    private Date afterAddedDate;
    @Option(names = "--done-after", descriptionKey = "ls.after.done.desc")
    private Date afterDoneDate;
    @Option(names = "--after", descriptionKey = "ls.after.desc")
    private List<String> afterTagList = new ArrayList<>();
    @Parameters(descriptionKey = "list.filters.desc", split = ",")
    private List<String> andFilterList = new ArrayList<>();
    @Option(names = "--added-before", descriptionKey = "ls.before.added.desc")
    private Date beforeAddedDate;
    @Option(names = "--done-before", descriptionKey = "ls.before.done.desc")
    private Date beforeDoneDate;
    @Option(names = "--before", descriptionKey = "ls.before.desc")
    private List<String> beforeTagList = new ArrayList<>();
    @ArgGroup
    private Exclusive exclusive = new Exclusive();
    @Option(names = "--clean", descriptionKey = "ls.clean.desc")
    private boolean isClean = false;
    @Option(names = "--not", descriptionKey = "ls.not.desc", split = ",")
    private List<String> notFilterList = new ArrayList<>();
    @Option(names = "--or", descriptionKey = "ls.or.desc", split = ",")
    private List<String> orFilterList = new ArrayList<>();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Inject
    private Path todoPath;
    @Inject
    private Repository<Integer, Todo> todoRepository;
    @Inject
    private TodoTextCleaner todoTextCleaner;
    @Inject
    private StyleApplicator styleApplicator;

    private int exitCode = 0;
    @Override
    public int getExitCode() {
        return exitCode;
    }

    private void list(final Repository<Integer, Todo> repository) {
        Specification<Integer, Todo> specification = new Any<>();

        if (exclusive.inclDone) {
            specification = specification.and(new IsDone());
        }
        if (exclusive.inclRemoved) {
            specification = specification.and(new IsRemoved());
        }
        if (exclusive.inclParked) {
            specification = specification.and(new IsParked());
        }

        if (afterAddedDate != null) {
            specification = specification.and(new IsAfterAddedDate(afterAddedDate));
        }
        if (beforeAddedDate != null) {
            specification = specification.and(new IsBeforeAddedDate(beforeAddedDate));
        }
        if (afterDoneDate != null) {
            specification = specification.and(new IsAfterDoneDate(afterDoneDate));
        }
        if (beforeDoneDate != null) {
            specification = specification.and(new IsBeforeDoneDate(beforeDoneDate));
        }
        for (String afterTag : afterTagList) {
            String[] afterTagSplit = afterTag.split(":");
            if (afterTagSplit.length == 2) {
                String tag = afterTagSplit[0];
                Date date;
                try {
                    date = simpleDateFormat.parse(afterTagSplit[1]);
                } catch (ParseException e) {
                    Logger.debug(e);
                    exitCode = 1;
                    spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_DATE_FORMAT,
                            gson.fromJson(String.format("{date:'%s'}", afterTagSplit[1]), Map.class)));
                    continue;
                }
                specification = specification.and(new IsAfterTagDate(tag, date));
            } else {
                Logger.debug("Unknown after tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_TAG_FORMAT,
                        gson.fromJson(String.format("{tag:'%s'}", afterTag), Map.class)));
            }
        }
        for (String beforeTag : beforeTagList) {
            String[] beforeTagSplit = beforeTag.split(":");
            if (beforeTagSplit.length == 2) {
                String tag = beforeTagSplit[0];
                Date date;
                try {
                    date = simpleDateFormat.parse(beforeTagSplit[1]);
                } catch (ParseException e) {
                    Logger.debug(e);
                    exitCode = 1;
                    spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_DATE_FORMAT,
                            gson.fromJson(String.format("{date:'%s'}", beforeTagSplit[1]), Map.class)));
                    continue;
                }
                specification = specification.and(new IsBeforeTagDate(tag, date));
            } else {
                Logger.debug("Unknown before tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_TAG_FORMAT,
                        gson.fromJson(String.format("{tag:'%s'}", beforeTag), Map.class)));
            }
        }

        for (String filter : andFilterList) {
            specification = specification.and(new IsContaining(filter));
        }
        for (String filter : orFilterList) {
            specification = specification.or(new IsContaining(filter));
        }
        for (String filter : notFilterList) {
            specification = specification.not(new IsContaining(filter));
        }

        Logger.debug(specification);

        List<Todo> todoList = repository.findAll(specification);
        if (todoList.isEmpty()) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.NO_TODO_FOUND));
        }
        todoList.forEach(todo -> {
            String todoText = isClean ? todoTextCleaner.clean(todo) : todo.getText();
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.TODO_LINE_ITEM,
                    gson.fromJson(String.format("{idx:'%3s',todo:'%s'}", todo.getIdx(), styleApplicator.apply(todoText)), Map.class)));
        });
    }

    @Override
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE",
            justification = "Google Guice does not allow for null injection (todoPath)")
    public void run() {
        list(todoRepository);

        if (exclusive.inclDone) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_DONE_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("done.txt"), new TodoFileTypeBuilder()));
        }

        if (exclusive.inclRemoved) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_REMOVED_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("removed.txt"), new TodoFileTypeBuilder()));
        }

        if (exclusive.inclParked) {
            spec.commandLine().getOut().println(templatedResourceBundle.getString(ResourceBundleKey.LIST_PARKED_TITLE));
            list(new FileRepository<>(todoPath.getParent().resolve("parked.txt"), new TodoFileTypeBuilder()));
        }
    }

    private static class Exclusive {
        @Option(names = "--done", descriptionKey = "ls.done.desc")
        private boolean inclDone = false;
        @Option(names = "--parked", descriptionKey = "ls.parked.desc")
        private boolean inclParked = false;
        @Option(names = "--removed", descriptionKey = "ls.removed.desc")
        private boolean inclRemoved = false;
    }
}
