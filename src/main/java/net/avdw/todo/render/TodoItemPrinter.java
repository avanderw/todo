package net.avdw.todo.render;

import net.avdw.todo.item.TodoItem;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public class TodoItemPrinter {
    public void printWithIndex(final TodoItem todoItem) {
        System.out.println(String.format("[%s] %s", StringUtils.leftPad(todoItem.getIdx() + "", 3), todoItem));
    }
}
