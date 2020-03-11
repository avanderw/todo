package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.item.TodoItem;
import org.pmw.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TodoItemThemeApplicator {
    private static final int DATE_LENGTH = 10;
    private static final int PRIORITY_LENGTH = 3;

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final ThemeApplicator themeApplicator;

    @Inject
    TodoItemThemeApplicator(final ThemeApplicator themeApplicator) {
        this.themeApplicator = themeApplicator;
    }

    public String applyThemeTo(final TodoItem todoItem) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(todoItem.getRawValue());
        boolean startDate = false;

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("x") && sb.length() == 0) {
                sb.append(themeApplicator.complete(token + " " + scanner.next()));
            } else if (token.length() == DATE_LENGTH && token.startsWith("20")) {
                if (!startDate) {
                    sb.append(themeApplicator.start(token));
                    startDate = true;
                }
            } else if (token.startsWith("+")) {
                sb.append(themeApplicator.project(token));
            } else if (token.startsWith("@")) {
                sb.append(themeApplicator.context(token));
            } else if (token.startsWith("(") && token.length() == PRIORITY_LENGTH && token.endsWith(")")) {
                sb.append(themeApplicator.priority(token));
            } else if (token.contains(":")) {
                if (token.startsWith("due:")) {
                    try {
                        Date date = SIMPLE_DATE_FORMAT.parse(token.replace("due:", ""));
                        if (date.before(new Date())) {
                            sb.append(themeApplicator.postDue(token));
                        } else {
                            sb.append(themeApplicator.preDue(token));
                        }
                    } catch (ParseException e) {
                        Logger.error(e.getMessage());
                        Logger.debug("Could not parse the date to apply formatting, defaulting to green");
                        Logger.debug(e);
                        sb.append(themeApplicator.error(token));
                    }
                } else {
                    sb.append(themeApplicator.addon(token));
                }
            } else {
                sb.append(themeApplicator.txt(token));
            }

            if (scanner.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
