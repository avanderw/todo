package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.avdw.todo.add.AddEvent;
import picocli.CommandLine;

public class Main {
    public static final EventBus EVENT_BUS = new EventBus();

    public static void main(String[] args){
        EVENT_BUS.register(new Main());
        CommandLine.run(new Todo(), args);
    }

    @Subscribe public void test(AddEvent event) {
        System.out.println("testing event " + event);
    }
}
