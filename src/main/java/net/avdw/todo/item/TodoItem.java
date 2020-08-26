package net.avdw.todo.item;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import net.avdw.todo.priority.Priority;
import net.avdw.todo.theme.TodoItemThemeApplicator;
import org.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @see net.avdw.todo.Todo
 */
@Deprecated
public class TodoItem {
    private final int idx;
    private final String line;
    private final TodoItemTokenIdentifier todoItemTokenIdentifier;
    private final TodoItemThemeApplicator todoItemThemeApplicator;
    private final Set<String> contexts = new HashSet<>();
    private final Set<String> projects = new HashSet<>();
    private SimpleDateFormat simpleDateFormat;
    private boolean tokensHaveBeenCached = false;

    @Inject
    public TodoItem(@Assisted final int idx, @Assisted final String line, final TodoItemTokenIdentifier todoItemTokenIdentifier, final TodoItemThemeApplicator todoItemThemeApplicator, final SimpleDateFormat simpleDateFormat) {
        this.idx = idx;
        this.line = line;
        this.todoItemTokenIdentifier = todoItemTokenIdentifier;
        this.todoItemThemeApplicator = todoItemThemeApplicator;
        this.simpleDateFormat = simpleDateFormat;
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
        return todoItemThemeApplicator.applyThemeTo(this);
    }

    public String getRawValue() {
        return line;
    }

    public boolean isInProgress() {
        return isStarted();
    }

    public boolean hasPriority() {
        return line.matches("^\\([A-Z]\\).*");
    }

    public Optional<Priority> getPriority() {
        if (!hasPriority()) {
            return Optional.empty();
        }

        return Optional.of(Priority.valueOf(line.substring(line.indexOf("(") + 1, line.indexOf(")"))));
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

    public String getMetaValueFor(final String key) {
        String metaValue;
        String formatKey = String.format("%s:", key);
        int metaKeyIdx = line.indexOf(formatKey);
        if (metaKeyIdx == -1) {
            Logger.trace("Cannot find meta key: {}", key);
            throw new UnsupportedOperationException();
        }

        String value = line.substring(metaKeyIdx);
        value = value.substring(formatKey.length());
        int spaceIdx = value.indexOf(" ");
        if (spaceIdx == -1) {
            metaValue = value;
        } else {
            metaValue = value.substring(0, spaceIdx);
        }
        return metaValue;
    }

    public Date getCreatedDate() {
        String cleanLine;
        if (isComplete()) {
            cleanLine = line.replaceFirst("x \\d\\d\\d\\d-\\d\\d-\\d\\d ", "");
        } else if (hasPriority()) {
            cleanLine = line.replaceFirst("\\([A-Z]\\) ", "");
        } else {
            cleanLine = line;
        }

        String createdDate = cleanLine.substring(0, cleanLine.indexOf(" "));
        try {
            return simpleDateFormat.parse(createdDate);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public Optional<Date> getDoneDate() {
        if (isComplete()) {
            String cleanLine = line.replaceFirst("x ", "");
            String dateString = cleanLine.substring(0, cleanLine.indexOf(" "));
            try {
                return Optional.of(simpleDateFormat.parse(dateString));
            } catch (ParseException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
