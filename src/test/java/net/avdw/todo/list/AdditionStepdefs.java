package net.avdw.todo.list;

import com.google.inject.*;
import cucumber.api.java8.En;
import net.avdw.todo.list.addition.AListAddition;
import net.avdw.todo.list.addition.ListAddition;
import net.avdw.todo.eventbus.EventBusModule;
import net.avdw.todo.repository.ARepository;
import net.avdw.todo.repository.memory.MemoryTask;
import net.avdw.todo.repository.memory.MemoryTaskRepository;
import net.avdw.todo.repository.model.ATask;

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
            addedTask = injector.getInstance(AListAddition.class).add(UUID.randomUUID().toString());
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
            install(new EventBusModule("Test List Addition"));
            bind(AListAddition.class).to(ListAddition.class);
            bind(new TypeLiteral<ARepository<ATask>>(){}).annotatedWith(MemoryTask.class).to(MemoryTaskRepository.class).in(Singleton.class);
        }
    }
}
