package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import net.avdw.todo.add.AddModule;
import net.avdw.todo.done.DoneModule;
import net.avdw.todo.list.ListApi;
import net.avdw.todo.list.ListTodo;
import net.avdw.todo.priority.PriorityModule;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.remove.RemoveModule;
import net.avdw.todo.replace.ReplaceModule;
import net.avdw.todo.tracking.TrackModule;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {


        install(new EventBusModule());
        install(new LoggingModule());
        install(new PropertyModule());

        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        bind(String.class).annotatedWith(Names.named("WUNDERLIST_NAME")).toInstance("todo.txt-sync");
        bind(ListApi.class).to(ListTodo.class);

        install(new AddModule());
        install(new DoneModule());
        install(new PriorityModule());
        install(new RemoveModule());
        install(new ReplaceModule());
        install(new TrackModule());
        //install(new WunderlistModule());
    }

    @Provides
    File todoFile() {
        File todoFile = new File(String.format("%s/.todo/todo.txt", System.getProperty("user.home")));
        File todoDir = new File(String.format("%s/.todo", System.getProperty("user.home")));
        if (!todoFile.exists()) {
            if (!todoDir.exists() && !todoDir.mkdirs()){
                Logger.warn(String.format("Could not create directories %s", todoDir));
            }
            try {
                if (!todoFile.createNewFile()) {
                    Logger.warn(String.format("Could not create file %s", todoFile));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        return todoFile;
    }
}