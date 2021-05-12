package net.avdw.todo.domain;

import net.avdw.todo.repository.AbstractSpecification;
import org.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class IsBeforeTagDate extends AbstractSpecification<Integer, Todo> {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final String tag;
    private final Date date;

    public IsBeforeTagDate(final String tag, final Date date) {
        this.tag = tag;
        this.date = new Date(date.getTime());
    }

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        final List<String> tagValueList = todo.getExtValueList(tag);
        if (tagValueList.isEmpty()) {
            Logger.trace("No tag {} found in todo ({})", tag, todo);
            return false;
        }

        boolean satisfied = true;
        for (final String tagValue : tagValueList) {
            try {
                satisfied = satisfied && simpleDateFormat.parse(tagValue).before(date);
            } catch (final ParseException e) {
                Logger.debug(e);
                satisfied = false;
            }
        }
        return satisfied;
    }

    @Override
    public String toString() {
        return String.format("isBeforeAddedDate('%tF')", date);
    }
}
