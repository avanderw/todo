package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.avdw.todo.add.AddEvent;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import picocli.CommandLine;

public class Main {
    public static final EventBus EVENT_BUS = new EventBus();

    public static void main(String[] args){
        EVENT_BUS.register(new Main());
        Logger.getConfiguration().level(Level.TRACE).activate();
        CommandLine.run(Todo.class, new GuiceFactory(), args);
    }

}
