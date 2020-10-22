package net.avdw.todo.core.mixin;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResource;
import net.avdw.todo.domain.IsAfterAddedDate;
import net.avdw.todo.domain.IsAfterChangedDate;
import net.avdw.todo.domain.IsAfterDoneDate;
import net.avdw.todo.domain.IsAfterTagDate;
import net.avdw.todo.domain.IsBeforeAddedDate;
import net.avdw.todo.domain.IsBeforeChangedDate;
import net.avdw.todo.domain.IsBeforeDoneDate;
import net.avdw.todo.domain.IsBeforeTagDate;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
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

public class DateFilterMixin implements Filter<Integer, Todo> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Option(names = "--added-after", descriptionKey = "list.after.added.desc", paramLabel = "yyyy-mm-dd")
    private Date afterAddedDate;
    @Option(names = "--changed-after", descriptionKey = "list.after.change.desc", paramLabel = "yyyy-mm-dd")
    private Date afterChangeDate;
    @Option(names = "--done-after", descriptionKey = "list.after.done.desc", paramLabel = "yyyy-mm-dd")
    private Date afterDoneDate;
    @Option(names = "--after-tag", descriptionKey = "list.after.desc", paramLabel = "tag:yyyy-mm-dd")
    private List<String> afterTagList = new ArrayList<>();
    @Option(names = "--added-before", descriptionKey = "list.before.added.desc", paramLabel = "yyyy-mm-dd")
    private Date beforeAddedDate;
    @Option(names = "--changed-before", descriptionKey = "list.before.change.desc", paramLabel = "yyyy-mm-dd")
    private Date beforeChangeDate;
    @Option(names = "--done-before", descriptionKey = "list.before.done.desc", paramLabel = "yyyy-mm-dd")
    private Date beforeDoneDate;
    @Option(names = "--before-tag", descriptionKey = "list.before.desc", paramLabel = "tag:yyyy-mm-dd")
    private List<String> beforeTagList = new ArrayList<>();
    @Spec
    private CommandSpec spec;
    @Inject
    private TemplatedResource templatedResource;

    public Specification<Integer, Todo> specification() {
        Specification<Integer, Todo> specification = new Any<>();
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
                    spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.INVALID_DATE_FORMAT,
                            String.format("{date:'%s'}", afterTagSplit[1])));
                    continue;
                }
                specification = specification.and(new IsAfterTagDate(tag, date));
            } else {
                Logger.debug("Unknown after tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.INVALID_TAG_FORMAT,
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
                    spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.INVALID_DATE_FORMAT,
                            String.format("{date:'%s'}", beforeTagSplit[1])));
                    continue;
                }
                specification = specification.and(new IsBeforeTagDate(tag, date));
            } else {
                Logger.debug("Unknown before tag ({}) should be tag:value");
                spec.commandLine().getErr().println(templatedResource.populateKey(ResourceBundleKey.INVALID_TAG_FORMAT,
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
