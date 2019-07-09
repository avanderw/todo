package net.avdw.todo.list.addition;

import com.google.inject.AbstractModule;

import java.text.SimpleDateFormat;

public class AdditionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AAddition.class).to(Addition.class);
        bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
    }
}
