package net.avdw.todo.add;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

public class AddWunderlist {

    @Subscribe
    public void test(AddEvent event) {
        System.out.println("testing event " + event);
    }
}
