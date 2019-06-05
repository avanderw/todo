package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import net.avdw.todo.list.addition.AddModule;
import net.avdw.todo.list.completion.DoneModule;
import net.avdw.todo.eventbus.EventBusModule;
import net.avdw.todo.list.filtering.ListApi;
import net.avdw.todo.list.filtering.ListTodo;
import net.avdw.todo.list.prioritisation.PriorityModule;
import net.avdw.todo.config.PropertyModule;
import net.avdw.todo.list.removal.RemoveModule;
import net.avdw.todo.list.rewriting.ReplaceModule;
import net.avdw.todo.list.tracking.TrackingModule;
import net.avdw.todo.repository.RepositoryModule;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {


        install(new LoggingModule());
        install(new PropertyModule());
        install(new EventBusModule("Todo"));
        install(new RepositoryModule());

        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        bind(String.class).annotatedWith(Names.named("WUNDERLIST_NAME")).toInstance("todo.lists-sync");
        bind(ListApi.class).to(ListTodo.class);

        install(new AddModule());
        install(new DoneModule());
        install(new PriorityModule());
        install(new RemoveModule());
        install(new ReplaceModule());
        install(new TrackingModule());
        //install(new WunderlistModule());
    }

    @Provides
    File todoFile() {
        File todoFile = new File(String.format("%s/.todo/todo.lists", System.getProperty("user.home")));
        File todoDir = new File(String.format("%s/.todo", System.getProperty("user.home")));
        if (!todoFile.exists()) {
            if (!todoDir.exists() && !todoDir.mkdirs()){
                Logger.warn(String.format("Could not create directories %s", todoDir));
            }
            try {
                if (!todoFile.createNewFile()) {
                    Logger.warn(String.format("Could not create plaintext %s", todoFile));
                }
            } catch (IOException e) {
                Logger.error(e);
            }
        }
        return todoFile;
    }
}