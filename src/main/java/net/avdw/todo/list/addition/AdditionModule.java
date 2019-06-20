package net.avdw.todo.list.addition;

import com.google.inject.AbstractModule;

public class AdditionModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AAddition.class).to(Addition.class);
        //bind(AddTodoTxt.class).asEagerSingleton();
    }
}
