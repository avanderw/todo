package net.avdw.todo;

import net.avdw.todo.action.TodoPriority;

import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class TodoItem {
    private final String line;
    private static final int DATE_LENGTH = 10;
    private static final int PRIORITY_LENGTH = 3;

    public TodoItem(final String line) {
        this.line = line;
    }

    public boolean isNotDone() {
        return !isDone();
    }

    public boolean isDone() {
        return line.startsWith("x ");
    }

    public boolean isStarted() {
        return line.contains("start:");
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
                sb.append(Ansi.GREEN);
            }
            if (token.length() == DATE_LENGTH && token.startsWith("20")) {
                if (sb.length() > Ansi.GREEN.length() &&
                        isDone() &&
                        previousToken.length() != DATE_LENGTH &&
                        !previousToken.startsWith("20")) {
                    if (!completedDate) {
                        sb.append(Ansi.GREEN);
                        completedDate = true;
                    }
                } else {
                    if (!startDate) {
                        sb.append(Ansi.WHITE);
                        startDate = true;
                    }
                }
            } else if (token.startsWith("+")) {
                sb.append(Ansi.MAGENTA);
            } else if (token.startsWith("@")) {
                sb.append(Ansi.CYAN);
            } else if (token.startsWith("(") && token.length() == PRIORITY_LENGTH && token.endsWith(")")) {
                sb.append(Ansi.YELLOW);
            } else if (token.contains(":")) {
                sb.append(Ansi.RED);
            }

            sb.append(token);

            if (scanner.hasNext()) {
                sb.append(" ");
            }
            sb.append(Ansi.RESET);
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

    public String rawValue() {
        return line;
    }

    public boolean isInProgress() {
        return isStarted();
    }

    public boolean hasPriority() {
        return line.matches("^\\([A-Z]\\).*");
    }

    public Optional<TodoPriority.Priority> getPriority() {
        if (!hasPriority()) {
            return Optional.empty();
        }

        return Optional.of(TodoPriority.Priority.valueOf(line.substring(line.indexOf("(") + 1, line.indexOf(")"))));
    }
}
