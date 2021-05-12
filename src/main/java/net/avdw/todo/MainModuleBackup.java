package net.avdw.todo;

import dagger.Module;
import dagger.Provides;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;

import javax.inject.Singleton;
import java.nio.file.Path;

@Module
class MainModuleBackup {
    protected void configure() {
//        bind(RunningStats.class).toInstance(new RunningStats());
//        bind(List.class).to(LinkedList.class);
//        bind(Set.class).to(HashSet.class);
//        bind(ResourceBundle.class).toInstance(ResourceBundle.getBundle("messages", Locale.ENGLISH));
//
//        install(new StyleModule());
//        install(new MixinModule());
//        install(new GroupByModule());
//        install(new SelectorModule());
//        install(new PostAddonModule());
    }

    @Provides
    @Singleton
    Repository<Integer, Todo> todoRepository(final Path todoPath) {
        return new FileRepository<>(todoPath, new TodoFileTypeBuilder());
    }
}
