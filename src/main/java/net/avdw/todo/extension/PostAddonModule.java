package net.avdw.todo.extension;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import net.avdw.todo.extension.recur.RecurDonePostAddon;

public class PostAddonModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder<PostAddon> addons = Multibinder.newSetBinder(binder(), PostAddon.class);
        addons.addBinding().to(RecurDonePostAddon.class);
    }
}
