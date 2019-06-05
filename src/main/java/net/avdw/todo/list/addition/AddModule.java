package net.avdw.todo.list.addition;

import com.google.inject.AbstractModule;

public class AddModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AListAddition.class).to(ListAddition.class);
        //bind(AddTodoTxt.class).asEagerSingleton();
    }
}
