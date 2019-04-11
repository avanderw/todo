package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import net.avdw.todo.wunderlist.WunderlistClient;

public class AddWunderlist implements AddApi {
    private WunderlistClient client;

    @Inject
    AddWunderlist(WunderlistClient client) {
        this.client = client;
    }

    @Subscribe
    public void add(AddEvent event) {
        client.createDatabase();
    }

    @Override
    public void add(String todo) {
        add(new AddEvent(todo));
    }
}
