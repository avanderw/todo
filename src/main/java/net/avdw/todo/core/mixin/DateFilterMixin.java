package net.avdw.todo.core.mixin;

import net.avdw.todo.core.RelativeDate;
import net.avdw.todo.domain.*;
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

    @Option(names = {"--after", "--since"}, description = "Inclusive (added)|(done)|(tag):<param>", paramLabel = "yyyy-mm-dd | +1w")
    private String after;
    @Option(names = "--before", description = "Exclusive (added)|(done)|(tag):<param>", paramLabel = "yyyy-mm-dd | +1w")
    private String before;

    @Spec
    private CommandSpec spec;

    public Specification<Integer, Todo> specification() {
        Specification<Integer, Todo> specification = new Any<>();
        int exitCode = 0;
        try {
            if (before != null) {
                final String type;
                final Date date;
                if (before.matches("\\S+:\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                    type = before.substring(0, before.indexOf(":"));
                    date = simpleDateFormat.parse(before.substring(type.length() + 1));
                } else if (before.matches("\\S+:[+-]\\d+[ymwd]")) {
                    type = before.substring(0, before.indexOf(":"));
                    date = new RelativeDate(before.substring(type.length() + 1));
                } else if (before.matches("\\S+:now")) {
                    type = before.substring(0, before.indexOf(":"));
                    date = new Date();
                } else {
                    throw new UnsupportedOperationException();
                }
                specification = specification.and(switch (type) {
                    case "added" -> new IsBeforeAddedDate(date);
                    case "removed" -> new IsBeforeRemovedDate(date);
                    case "parked" -> new IsBeforeParkedDate(date);
                    case "done" -> new IsBeforeDoneDate(date);
                    default -> new IsBeforeTagDate(type, date);
                });
            }
        } catch (final UnsupportedOperationException | ParseException e) {
            exitCode = 1;
            spec.commandLine().getErr().printf("--before cannot parse '%s' should be in [type]:(yyyy-mm-dd)|(+1w) format%n", before);
        }

        try {
            if (after != null) {
                final String type;
                final Date date;
                if (after.matches("\\S+:\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
                    type = after.substring(0, after.indexOf(":"));
                    date = simpleDateFormat.parse(after.substring(type.length() + 1));
                } else if (after.matches("\\S+:[+-]\\d+[ymwd]")) {
                    type = after.substring(0, after.indexOf(":"));
                    date = new RelativeDate(after.substring(type.length() + 1));
                } else if (after.matches("\\S+:now")) {
                    type = after.substring(0, after.indexOf(":"));
                    date = new Date();
                } else {
                    throw new UnsupportedOperationException();
                }
                specification = specification.and(switch (type) {
                    case "added" -> new IsAfterAddedDate(date);
                    case "removed" -> new IsAfterRemovedDate(date);
                    case "parked" -> new IsAfterParkedDate(date);
                    case "done" -> new IsAfterDoneDate(date);
                    default -> new IsAfterTagDate(type, date);
                });
            }
        } catch (final UnsupportedOperationException | ParseException e) {
            exitCode = 1;
            spec.commandLine().getErr().printf("--after cannot parse '%s' should be in [type]:(yyyy-mm-dd)|(+1w) format%n", after);
        }

        if (exitCode != 0) {
            throw new UnsupportedOperationException();
        }
        return specification;
    }
}
