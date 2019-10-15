package net.avdw.todo.item;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.avdw.todo.AnsiColor;
import net.avdw.todo.action.TodoPriority;
import org.pmw.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TodoItem {
    private int idx;
    private final String line;
    private TodoItemTokenIdentifier todoItemTokenIdentifier;

    private final Set<String> contexts = new HashSet<>();
    private final Set<String> projects = new HashSet<>();
    private boolean tokensHaveBeenCached = false;
    private static final int DATE_LENGTH = 10;
    private static final int PRIORITY_LENGTH = 3;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Inject
    public TodoItem(@Assisted final int idx, @Assisted final String line, final TodoItemTokenIdentifier todoItemTokenIdentifier) {
        this.idx = idx;
        this.line = line;
        this.todoItemTokenIdentifier = todoItemTokenIdentifier;
    }

    public boolean isIncomplete() {
        return !isComplete();
    }

    public boolean isComplete() {
        return line.startsWith("x ");
    }

    public boolean isStarted() {
        return line.contains("start:");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format("[%s%2s%s] ", AnsiColor.BLUE, idx, AnsiColor.RESET));
        Scanner scanner = new Scanner(line);
        String previousToken = "";
        boolean completedDate = false;
        boolean startDate = false;
        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("x") && sb.length() == 0) {
                sb.append(AnsiColor.GREEN);
            }
            if (token.length() == DATE_LENGTH && token.startsWith("20")) {
                if (sb.length() > AnsiColor.GREEN.length() &&
                        isComplete() &&
                        previousToken.length() != DATE_LENGTH &&
                        !previousToken.startsWith("20")) {
                    if (!completedDate) {
                        sb.append(AnsiColor.GREEN);
                        completedDate = true;
                    }
                } else {
                    if (!startDate) {
                        sb.append(AnsiColor.WHITE);
                        startDate = true;
                    }
                }
            } else if (token.startsWith("+")) {
                sb.append(AnsiColor.MAGENTA);
            } else if (token.startsWith("@")) {
                sb.append(AnsiColor.CYAN);
            } else if (token.startsWith("(") && token.length() == PRIORITY_LENGTH && token.endsWith(")")) {
                sb.append(AnsiColor.YELLOW);
            } else if (token.contains(":")) {
                if (token.startsWith("due:")) {
                    try {
                        Date date = SIMPLE_DATE_FORMAT.parse(token.replace("due:", ""));
                        if (date.before(new Date())) {
                            sb.append(AnsiColor.RED);
                        } else {
                            sb.append(AnsiColor.GREEN);
                        }
                    } catch (ParseException e) {
                        Logger.error(e.getMessage());
                        Logger.debug("Could not parse the date to apply formatting, defaulting to green");
                        Logger.debug(e);
                        sb.append(AnsiColor.GREEN);
                    }
                } else {
                    sb.append(AnsiColor.RED);
                }
            }

            sb.append(token);

            if (scanner.hasNext()) {
                sb.append(" ");
            }
            sb.append(AnsiColor.RESET);
            previousToken = token;
        }
        return sb.toString();
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

    public Set<String> getProjects() {
        cacheTokens();
        return projects;
    }

    public Set<String> getContexts() {
        cacheTokens();
        return contexts;
    }

    private void cacheTokens() {
        if (tokensHaveBeenCached) {
            return;
        }

        Scanner scanner = new Scanner(line);
        while (scanner.hasNext()) {
            String token = scanner.next();
            switch (todoItemTokenIdentifier.identify(token)) {
                case PROJECT:
                    projects.add(token.substring(1));
                    break;
                case CONTEXT:
                    contexts.add(token.substring(1));
                    break;
                case NORMAL:
                    break;
                default:
                    Logger.warn(String.format("Unidentified token '%s' for '%s'", token, this));
            }
        }

        tokensHaveBeenCached = true;
    }

    public boolean hasContext() {
        return !getContexts().isEmpty();
    }

    public boolean hasProjects() {
        return !getProjects().isEmpty();
    }
}
