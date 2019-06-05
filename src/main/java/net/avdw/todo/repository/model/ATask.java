package net.avdw.todo.repository.model;

import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ATask extends AItem {
    private final String creationDate;
    private String summary;

    @Inject
    ATask(SimpleDateFormat simpleDateFormat) {
        creationDate = simpleDateFormat.format(new Date());
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
