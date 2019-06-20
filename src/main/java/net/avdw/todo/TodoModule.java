package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import net.avdw.todo.admin.initialize.InitializeModule;
import net.avdw.todo.eventbus.EventBusModule;
import net.avdw.todo.list.addition.AdditionModule;
import net.avdw.todo.list.filtering.FilteringModule;
import net.avdw.todo.property.PropertyModule;
import net.avdw.todo.repository.RepositoryModule;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LoggingModule());
        install(new PropertyModule(Paths.get(System.getProperty("user.home"))));
        install(new EventBusModule("Main"));
        install(new RepositoryModule(Paths.get(".")));

        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
//        bind(String.class).annotatedWith(Names.named("WUNDERLIST_NAME")).toInstance("todo.lists-sync");
//        bind(ListApi.class).to(ListTodo.class);

        install(new InitializeModule());
        install(new FilteringModule());
        install(new AdditionModule());
//        install(new DoneModule());
//        install(new PriorityModule());
//        install(new RemoveModule());
//        install(new ReplaceModule());
//        install(new TrackingModule());
        //install(new WunderlistModule());
    }
//
//    @Provides
//    File todoFile() {
//        File todoFile = new File(String.format("%s/.todo/todo.lists", System.getProperty("user.home")));
//        File todoDir = new File(String.format("%s/.todo", System.getProperty("user.home")));
//        if (!todoFile.exists()) {
//            if (!todoDir.exists() && !todoDir.mkdirs()){
//                Logger.warn(String.format("Could not create directories %s", todoDir));
//            }
//            try {
//                if (!todoFile.createNewFile()) {
//                    Logger.warn(String.format("Could not create file %s", todoFile));
//                }
//            } catch (IOException e) {
//                Logger.error(e);
//            }
//        }
//        return todoFile;
//    }
}