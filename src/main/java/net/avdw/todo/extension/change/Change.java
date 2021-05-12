package net.avdw.todo.extension.change;

import java.util.Date;

public class Change {
    private final Date date;
    private final String type;

    public Change(final String type, final Date date) {
        this.date = new Date(date.getTime());
        this.type = type;
    }

    public Change(final String type) {
        this.type = type;
        this.date = null;
    }

    public Date getDate() {
        return date == null ? null : new Date(date.getTime());
    }

    public String getType() {
        return type;
    }

}
