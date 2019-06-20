package net.avdw.todo.list;

import com.google.inject.*;
import cucumber.api.java8.En;
import net.avdw.todo.list.filtering.*;
import net.avdw.todo.repository.RepositoryModule;
import net.avdw.todo.repository.file.FileTask;
import net.avdw.todo.repository.memory.MemoryTaskRepositoryModule;
import net.avdw.todo.repository.model.ATask;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class FilteringStepdefs implements En {
    private List<String> list;
    private Injector injector;

    public FilteringStepdefs() {
        Given("^the task repository \"([^\"]*)\"$", (String repository) -> {
            injector = Guice.createInjector(new Config(Paths.get(repository)));
        });
        When("^I list the todo items$", () -> {
            list = injector.getInstance(Key.get(AFilter.class, TodoList.class)).list();
        });
        Then("^the list will contain (\\d+) items$", (Integer num) -> {
            assertThat(list, hasSize(num));
        });
        When("^I list the todo items with filter \"([^\"]*)\"$", (String filter) -> {
            String[] filters = filter.split("\\s");
            list = injector.getInstance(Key.get(AFilter.class, TodoList.class)).list(Arrays.asList(filters));
        });
        When("^I list the contexts$", () -> {
            list = injector.getInstance(Key.get(AFilter.class, Context.class)).list();
        });
        When("^I list the projects$", () -> {
            list = injector.getInstance(Key.get(AFilter.class, Project.class)).list();
        });
        Then("^the list will be \\[([^\"]*)\\]$", (String listString) -> {
            String[] testList = listString.split(",");
            assertThat(list, containsInAnyOrder(testList));
        });
    }

    class Config extends AbstractModule {
        private Path path;

        Config(Path path) {
            this.path = path;
        }

        @Override
        protected void configure() {
            install(new FilteringModule());
            install(new RepositoryModule(path));
        }
    }
}
