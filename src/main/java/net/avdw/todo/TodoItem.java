package net.avdw.todo;

import net.avdw.todo.action.TodoPriority;
import org.pmw.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TodoItem {
    private int idx;
    private final String line;
    private static final int DATE_LENGTH = 10;
    private static final int PRIORITY_LENGTH = 3;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public TodoItem(final int idx, final String line) {
        this.idx = idx;
        this.line = line;
    }

    public boolean isIncomplete() {
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
                if (token.startsWith("due:")) {
                    try {
                        Date date = SIMPLE_DATE_FORMAT.parse(token.replace("due:", ""));
                        if (date.before(new Date())) {
                            sb.append(Ansi.RED);
                        } else {
                            sb.append(Ansi.GREEN);
                        }
                    } catch (ParseException e) {
                        Logger.error(e.getMessage());
                        Logger.debug("Could not parse the date to apply formatting, defaulting to green");
                        Logger.debug(e);
                        sb.append(Ansi.GREEN);
                    }
                } else {
                    sb.append(Ansi.RED);
                }
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
        Set<String> contexts = new HashSet<>();
        Scanner scanner = new Scanner(line);
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("@")) {
                contexts.add(token.substring(1));
            }
        }
        return contexts;
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

    public int getIdx() {
        return idx;
    }

    public boolean hasContext() {
        return !getContexts().isEmpty();
    }

    public boolean hasProjects() {
        return !getProjects().isEmpty();
    }
}
