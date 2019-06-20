package net.avdw.todo.list;

import com.google.inject.*;
import cucumber.api.java8.En;
import net.avdw.todo.LoggingModule;
import net.avdw.todo.list.addition.AAddition;
import net.avdw.todo.list.addition.AdditionModule;
import net.avdw.todo.eventbus.EventBusModule;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.RepositoryModule;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.model.ATask;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class AdditionStepdefs implements En {
    private int addedTaskCount = 0;
    private ATask addedTask;

    public AdditionStepdefs() {
        final Injector injector = Guice.createInjector(new Config());
        final String today = injector.getInstance(SimpleDateFormat.class).format(new Date());

        When("^a task is added$", () -> {
            addedTaskCount = injector.getInstance(Key.get(new TypeLiteral<ARepository<ATask>>(){}, MemoryTask.class)).list().size();
            addedTask = injector.getInstance(AAddition.class).add(UUID.randomUUID().toString());
            addedTaskCount++;
        });
        Then("^the list added to will contain an additional task$", () -> {
            assertThat(injector.getInstance(Key.get(new TypeLiteral<ARepository<ATask>>(){}, MemoryTask.class)).list(), hasSize(addedTaskCount));
        });
        Then("^the added task's creation date will be today$", () -> {
            assertThat(addedTask, hasProperty("creationDate", equalTo(today)));
        });
    }

    class Config extends AbstractModule {
        @Override
        protected void configure() {
            install(new LoggingModule());
            install(new AdditionModule());
            install(new RepositoryModule(Paths.get("src/test/resources/lists/addition")));
        }
    }
}
