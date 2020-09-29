package net.avdw.todo;

import com.google.inject.Inject;
import net.avdw.todo.domain.*;
import net.avdw.todo.repository.Specification;
import org.tinylog.Logger;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateFilter {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Option(names = "--added-after", descriptionKey = "list.after.added.desc")
    private Date afterAddedDate;
    @Option(names = "--changed-after", descriptionKey = "list.before.change.desc")
    private Date afterChangeDate;
    @Option(names = "--done-after", descriptionKey = "list.after.done.desc")
    private Date afterDoneDate;
    @Option(names = "--after-tag", descriptionKey = "list.after.desc")
    private List<String> afterTagList = new ArrayList<>();
    @Option(names = "--added-before", descriptionKey = "list.before.added.desc")
    private Date beforeAddedDate;
    @Option(names = "--changed-before", descriptionKey = "list.before.change.desc")
    private Date beforeChangeDate;
    @Option(names = "--done-before", descriptionKey = "list.before.done.desc")
    private Date beforeDoneDate;
    @Option(names = "--before-tag", descriptionKey = "list.before.desc")
    private List<String> beforeTagList = new ArrayList<>();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResourceBundle templatedResourceBundle;

    public Specification<Integer, Todo> specification(Specification<Integer, Todo> specification) {
        int exitCode = 0;
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
        if (afterChangeDate != null) {
            specification = specification.and(new IsAfterChangedDate(afterChangeDate));
        }
        if (beforeChangeDate != null) {
            specification = specification.and(new IsBeforeChangedDate(beforeChangeDate));
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
                            String.format("{date:'%s'}", afterTagSplit[1])));
                    continue;
                }
                specification = specification.and(new IsAfterTagDate(tag, date));
            } else {
                Logger.debug("Unknown after tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_TAG_FORMAT,
                        String.format("{tag:'%s'}", afterTag)));
                exitCode = 1;
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
                            String.format("{date:'%s'}", beforeTagSplit[1])));
                    continue;
                }
                specification = specification.and(new IsBeforeTagDate(tag, date));
            } else {
                Logger.debug("Unknown before tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResourceBundle.getString(ResourceBundleKey.INVALID_TAG_FORMAT,
                        String.format("{tag:'%s'}", beforeTag)));
                exitCode = 1;
            }
        }
        if (exitCode != 0) {
            throw new UnsupportedOperationException();
        }
        return specification;
    }
}
