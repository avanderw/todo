package net.avdw.todo.plugin.change;

import lombok.Data;

import java.util.Date;

@Data
public class Change {
    private Date date;
    private String type;

    public Change(final String type, final Date date) {
        this.date = date;
        this.type = type;
    }
}
