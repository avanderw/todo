package net.avdw.todo.wunderlist;

import java.util.Date;

public class ListModel {
    Integer id;
    Date created_at;
    String title;
    String list_type;
    String type;
    Integer revision;

    @Override
    public String toString() {
        return "ListModel{" +
                "id=" + id +
                ", created_at=" + created_at +
                ", title='" + title + '\'' +
                ", list_type='" + list_type + '\'' +
                ", type='" + type + '\'' +
                ", revision=" + revision +
                '}';
    }
}
