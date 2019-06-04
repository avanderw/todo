package net.avdw.todo.add;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.wunderlist.WunderlistClientOld;

public class AddWunderlist implements AddApi {
    private WunderlistClientOld client;

    @Inject
    AddWunderlist(WunderlistClientOld client) {
        this.client = client;
    }

    @Subscribe
    public void add(AddEvent event) {
        client.createDatabase();
        client.addTask(event.todo);
    }

    @Override
    public void add(String todo) {
        add(new AddEvent(todo));
    }
}
