package net.avdw.todo;

import dagger.Component;
import net.avdw.todo.core.CoreModule;
import net.avdw.todo.extension.ExtensionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {TestMainModule.class, CoreModule.class, ExtensionModule.class})
public interface TestMainComponent extends MainComponent {
    @Component.Builder
    interface Builder {
        Builder testMainModule(TestMainModule testMainModule);

        TestMainComponent build();
    }
}
