package net.avdw.todo.repository.model;

import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.*;

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

    public Set<String> getProjects() {
        Set<String> projects = new HashSet<>();
        Scanner scanner  = new Scanner(summary);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("+")) {
                projects.add(token.substring(1));
            }
        }
        return projects;
    }

    public Set<String> getContexts() {
        Set<String> contexts = new HashSet<>();
        Scanner scanner  = new Scanner(summary);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("@")) {
                contexts.add(token.substring(1));
            }
        }
        return contexts;
    }

    public String getSummary() {
        return summary;
    }
}
