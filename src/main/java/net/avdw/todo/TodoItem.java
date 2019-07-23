package net.avdw.todo;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class TodoItem {
    private final String line;

    public TodoItem(String line) {
        this.line = line;
    }

    public boolean isNotDone() {
        return !isDone();
    }

    public boolean isDone() {
        return line.startsWith("x");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(line);
        String previousToken = "";
        boolean completedDate = false;
        boolean startDate = false;
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("x") && sb.length() == 0) {
                sb.append(Ansi.Green);
            }
            if (token.length() == 10 && token.startsWith("20")) {
                if (sb.length() > Ansi.Green.length() &&
                        isDone() &&
                        previousToken.length() != 10 &&
                        !previousToken.startsWith("20")) {
                    if (!completedDate) {
                        sb.append(Ansi.Green);
                        completedDate = true;
                    }
                } else {
                    if (!startDate) {
                        sb.append(Ansi.White);
                        startDate = true;
                    }
                }
            } else if (token.startsWith("+")) {
                sb.append(Ansi.Cyan);
            } else if (token.startsWith("@")) {
                sb.append(Ansi.Magenta);
            } else if (token.startsWith("(") && token.length() == 3 && token.endsWith(")")) {
                sb.append(Ansi.Yellow);
            } else if (token.startsWith("due:")) {
                sb.append(Ansi.Red);
            }

            sb.append(token);

            if (scanner.hasNext()) {
                sb.append(" ");
            }
            sb.append(Ansi.Reset);
            previousToken = token;
        }
        return sb.toString();
    }

    public Set<String> getProjects() {
        Set<String> projects = new HashSet<>();
        Scanner scanner = new Scanner(line);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("+")) {
                projects.add(token.substring(1));
            }
        }
        return projects;
    }

    public Set<String> getContexts() {
        Set<String> projects = new HashSet<>();
        Scanner scanner = new Scanner(line);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("@")) {
                projects.add(token.substring(1));
            }
        }
        return projects;
    }
}
