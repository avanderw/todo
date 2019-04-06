package net.avdw.todo.add;

import com.google.common.eventbus.Subscribe;

public class AddWunderlist {

    @Subscribe
    public void test(AddEvent event) {
        System.out.println("testing event " + event);
    }
}
