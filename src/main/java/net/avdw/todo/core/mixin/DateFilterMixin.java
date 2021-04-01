package net.avdw.todo.core.mixin;

import net.avdw.todo.domain.IsAfterAddedDate;
import net.avdw.todo.domain.IsAfterDoneDate;
import net.avdw.todo.domain.IsAfterTagDate;
import net.avdw.todo.domain.IsBeforeAddedDate;
import net.avdw.todo.domain.IsBeforeDoneDate;
import net.avdw.todo.domain.IsBeforeTagDate;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFilterMixin implements Filter<Integer, Todo> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Option(names = "--after", description = "Inclusive [(added)|(done)|(tag):]yyyy-mm-dd", paramLabel = "yyyy-mm-dd")
    private String after;
    @Option(names = "--before", description = "Exclusive [(added)|(done)|(tag):]yyyy-mm-dd", paramLabel = "yyyy-mm-dd")
    private String before;

    @Spec private CommandSpec spec;

    public Specification<Integer, Todo> specification() {
        Specification<Integer, Todo> specification = new Any<>();
        int exitCode = 0;

        if (before != null) {
            if (before.contains(":")) {
                String type = before.substring(0, before.indexOf(":"));
                try {
                    Date date = simpleDateFormat.parse(before.substring(type.length() + 1));
                    specification = specification.and(switch (type) {
                        case "added" -> new IsBeforeAddedDate(date);
                        case "done" -> new IsBeforeDoneDate(date);
                        default -> new IsBeforeTagDate(type, date);
                    });
                } catch (ParseException e) {
                    exitCode = 1;
                    spec.commandLine().getErr().printf("--before cannot parse '%s' should be in [type]:yyyy-mm-dd format%n", before);
                }
            }
        }

        if (after != null) {
            if (after.contains(":")) {
                String type = after.substring(0, after.indexOf(":"));
                try {
                    Date date = simpleDateFormat.parse(after.substring(type.length() + 1));
                    specification = specification.and(switch (type) {
                        case "added" -> new IsAfterAddedDate(date);
                        case "done" -> new IsAfterDoneDate(date);
                        default -> new IsAfterTagDate(type, date);
                    });
                } catch (ParseException e) {
                    exitCode = 1;
                    spec.commandLine().getErr().printf("--after cannot parse '%s' should be in [type]:yyyy-mm-dd format%n", after);
                }
            }
        }

        if (exitCode != 0) {
            throw new UnsupportedOperationException();
        }
        return specification;
    }
}
