package net.avdw.todo.render;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class TodoBarRenderer {

    @Inject
    TodoBarRenderer() {
    }

    /**
     * Build a ANSI bar showing which items are complete vs incomplete.
     *
     * @param size the list of todo to create the bar from
     * @return the ANSI colour coded bar
     */
    public String createBar(final int size) {
        return StringUtils.repeat(" ", size);
    }
}
