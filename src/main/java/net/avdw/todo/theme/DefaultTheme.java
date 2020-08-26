package net.avdw.todo.theme;

import com.google.inject.Inject;
import net.avdw.todo.RunningStats;
import net.avdw.todo.item.TodoItem;
import net.avdw.todo.item.TodoItemCleaner;
import org.apache.commons.lang3.StringUtils;
import org.tinylog.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DefaultTheme implements Theme {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private int lineLength;
    private RunningStats runningStats;
    private ColorPalette<String> colorPalette;
    private TodoItemCleaner todoItemCleaner;

    @Inject
    DefaultTheme(@LineLength final int lineLength, final RunningStats runningStats, final ColorPalette<String> colorPalette, final TodoItemCleaner todoItemCleaner) {
        this.lineLength = lineLength;
        this.runningStats = runningStats;
        this.colorPalette = colorPalette;
        this.todoItemCleaner = todoItemCleaner;
    }

    @Override
    public void printHeader(final String text) {
        System.out.print(colorPalette.primaryTint());
        System.out.println(StringUtils.center(String.format("< %s >", text), lineLength, "-"));
        System.out.print(colorPalette.primaryTone());
    }

    @Override
    public void printDuration() {
        System.out.print(colorPalette.primaryShade());
        System.out.println(StringUtils.leftPad(String.format("< %s >--", runningStats.getDuration()), lineLength, "-"));
        System.out.print(colorPalette.primaryTone());
    }

    @Override
    public void printCleanTodoItemWithoutIdx(final TodoItem todoItem) {
        System.out.println(themeTodoItem(todoItemCleaner.clean(todoItem)));
    }

    @Override
    public void printFullTodoItemWithIdx(final TodoItem todoItem) {
        System.out.println(String.format("[%3s] %s", todoItem.getIdx(), themeTodoItem(todoItem)));
    }

    @Override
    public void printDisplaySummary(final int showingSize, final int totalSize) {
        System.out.print(colorPalette.primaryTone());
        System.out.println(String.format("Showing [%3s] of [%3s] items", showingSize, totalSize));
        System.out.print(colorPalette.primaryTone());
    }

    private String themeTodoItem(final TodoItem todoItem) {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(todoItem.getRawValue());
        boolean startDate = false;

        while (scanner.hasNext()) {
            String token = scanner.next();
            if (token.startsWith("x") && sb.length() == 0) {
                sb.append(colorPalette.primaryTint());
                sb.append(token).append(" ").append(scanner.next());
                sb.append(colorPalette.primaryTone());
            } else if (token.length() == "xxxx-xx-xx".length() && token.startsWith("20")) {
                if (!startDate) {
                    sb.append(colorPalette.primaryShade());
                    sb.append(token);
                    sb.append(colorPalette.primaryTone());
                    startDate = true;
                }
            } else if (token.startsWith("+")) {
                sb.append(colorPalette.primaryTint());
                sb.append(token);
                sb.append(colorPalette.primaryTone());
            } else if (token.startsWith("@")) {
                sb.append(colorPalette.secondaryTone());
                sb.append(token);
                sb.append(colorPalette.primaryTone());
            } else if (token.startsWith("(") && token.length() == "(X)".length() && token.endsWith(")")) {
                sb.append(colorPalette.accentTone());
                sb.append(token);
                sb.append(colorPalette.primaryTone());
            } else if (token.contains(":")) {
                if (token.startsWith("due:")) {
                    try {
                        Date date = SIMPLE_DATE_FORMAT.parse(token.replace("due:", ""));
                        if (date.before(new Date())) {
                            sb.append(colorPalette.accentTone());
                            sb.append(token);
                            sb.append(colorPalette.primaryTone());
                        } else {
                            sb.append(colorPalette.primaryShade());
                            sb.append(token);
                            sb.append(colorPalette.primaryTone());
                        }
                    } catch (ParseException e) {
                        Logger.error(e.getMessage());
                        Logger.debug(e);
                        sb.append(colorPalette.accentTone());
                        sb.append(token);
                        sb.append(colorPalette.primaryTone());
                    }
                } else {
                    sb.append(colorPalette.secondaryTone());
                    sb.append(token);
                    sb.append(colorPalette.primaryTone());
                }
            } else {
                sb.append(colorPalette.primaryTone());
                sb.append(token);
            }

            if (scanner.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
