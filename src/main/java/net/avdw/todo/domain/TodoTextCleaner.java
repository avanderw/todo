package net.avdw.todo.domain;

import javax.inject.Inject;

public class TodoTextCleaner {
    @Inject
    public TodoTextCleaner() {
    }

    public String clean(final Todo todo) {
        String cleanText = todo.getText();

        cleanText = cleanText.replaceFirst("^\\([A-Z]\\)\\s", "");
        cleanText = cleanText.replaceFirst("^x \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanText = cleanText.replaceFirst("^r \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanText = cleanText.replaceFirst("^p \\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanText = cleanText.replaceFirst("^\\d\\d\\d\\d-\\d\\d-\\d\\d\\s", "");
        cleanText = cleanText.replaceAll("\\s\\S*:\\S*", "");
        cleanText = cleanText.replaceAll("(\\s?)(@)(\\S+\\s?)", "$1$3");
        cleanText = cleanText.replaceAll("(\\s?)(\\+)(\\S+\\s?)", "$1$3");
        cleanText = cleanText.replaceAll("([a-z])([A-Z]+)", "$1 $2");
        cleanText = cleanText.replaceAll("_", " ");

        return cleanText;
    }
}
