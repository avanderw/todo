package net.avdw.todo;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.avdw.todo.add.AddModule;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class TodoModule extends AbstractModule {
    @Override
    protected void configure() {
        EventBus eventBus = new EventBus("Todo EventBus");
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register((InjectionListener<I>) eventBus::register);
            }
        });
        bindInterceptor(Matchers.inSubpackage("net.avdw.todo"), Matchers.any(), new LoggingInterceptor());

        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        install(new AddModule());
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