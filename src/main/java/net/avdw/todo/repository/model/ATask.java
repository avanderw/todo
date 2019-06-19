package net.avdw.todo.repository.model;

import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ATask extends AItem {
    private String creationDate;
    private String summary;
    private final List<String> projects = new ArrayList<>();
    private final List<String> contexts = new ArrayList<>();

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

    public List<String> getProjects() {
        throw new UnsupportedOperationException();
    }

    public List<String> getContexts() {
        throw new UnsupportedOperationException();
    }

    public String getSummary() {
        return summary;
    }
}
