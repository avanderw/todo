package net.avdw.todo.repository.model;

import com.google.inject.Inject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATask extends AItem {
    private final String taskFormatRegex = "^(x)? ?(\\([A-Z]\\))? ?(\\d{4}-\\d{2}-\\d{2})? ?(\\d{4}-\\d{2}-\\d{2})? ?(.*)$";
    private final Pattern taskFormatPattern = Pattern.compile(taskFormatRegex);
    private String priority;
    private String creationDate;
    private String completionDate;
    private String summary;
    private boolean isComplete;
    private final SimpleDateFormat simpleDateFormat;

    @Inject
    ATask(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public void setSummary(String summary) {
        Matcher summaryMatcher = taskFormatPattern.matcher(summary);
        if (summaryMatcher.find()) {
            System.out.println(summaryMatcher.group(0));
            isComplete = summaryMatcher.group(1) != null;
            priority = summaryMatcher.group(2);
            completionDate = isComplete ? summaryMatcher.group(3) : null;
            creationDate = isComplete ? summaryMatcher.group(4) : summaryMatcher.group(3);
            if (creationDate == null) {
                creationDate = simpleDateFormat.format(new Date());
            }
            this.summary = summaryMatcher.group(5);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Set<String> getProjects() {
        Set<String> projects = new HashSet<>();
        Scanner scanner = new Scanner(summary);
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
        Scanner scanner = new Scanner(summary);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("@")) {
                contexts.add(token.substring(1));
            }
        }
        return contexts;
    }

    @Override
    public String toString() {
        StringBuilder taskString = new StringBuilder();
        if (isComplete) {
            taskString.append("x ");
            if (priority != null) {
                taskString.append(priority).append(" ");
            }
            taskString.append(completionDate).append(" ");
        }

        if (priority != null && !isComplete) {
            taskString.append(priority).append(" ");
        }

        taskString.append(creationDate).append(" ");
        taskString.append(summary);

        return taskString.toString();
    }
}
