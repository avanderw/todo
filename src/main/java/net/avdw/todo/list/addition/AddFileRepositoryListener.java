package net.avdw.todo.list.addition;

import com.google.common.eventbus.Subscribe;
import net.avdw.todo.eventbus.ListUpdatedEvent;

public class AddFileRepositoryListener {
    @Subscribe
    public void updateRepository(ListUpdatedEvent listUpdatedEvent) {
        throw new UnsupportedOperationException();
    }
}
