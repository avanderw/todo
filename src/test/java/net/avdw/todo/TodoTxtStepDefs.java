package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import cucumber.api.java8.En;
import net.avdw.todo.add.AddApi;
import net.avdw.todo.add.AddTodoTxt;
import net.avdw.todo.done.DoneApi;
import net.avdw.todo.done.DoneTodoTxt;
import net.avdw.todo.list.ListApi;
import net.avdw.todo.list.ListTodo;
import net.avdw.todo.priority.PriorityApi;
import net.avdw.todo.priority.PriorityInput;
import net.avdw.todo.priority.PriorityTodoTxt;
import net.avdw.todo.remove.RemoveApi;
import net.avdw.todo.remove.RemoveTodoTxt;
import net.avdw.todo.replace.ReplaceApi;
import net.avdw.todo.replace.ReplaceTodoTxt;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TodoTxtStepDefs implements En {
    private File file;
    private File done;
    private List<String> items;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public TodoTxtStepDefs() {
        Given("^I track the file \"([^\"]*)\"$", (String name) -> getModule().name = name);
        When("^I list the todo items with no arguments$", () -> items = getInjector().getInstance(ListApi.class).list());
        Then("^I will get a list with (\\d+) items$", (Integer num) -> assertThat(items.size(), is(equalTo(num))));
        When("^I list the todo items with arguments \"([^\"]*)\"$", (String args) -> items = getInjector().getInstance(ListApi.class).list(Arrays.asList(args.split(" "))));
        Given("^the file \"([^\"]*)\" does not exist$", (String path) -> {
            if (!new File(path).delete()) {
                Logger.debug(String.format("%s removed", path));
            }
        });
        Given("^I copy the file \"([^\"]*)\" to \"([^\"]*)\"$", (String from, String to) -> Files.copy(Paths.get(from), Paths.get(to)));
        When("^I add a todo item$", () -> getInjector().getInstance(AddApi.class).add(UUID.randomUUID().toString()));
        And("^the last item will have a created date of now$", () -> assertThat("Must start with today's date", items.get(items.size() - 1).startsWith(sdf.format(new Date()), 5)));
        When("^I remove item (\\d+)$", (Integer idx) -> getInjector().getInstance(RemoveApi.class).remove(idx));
        And("^item (\\d+) will be \"([^\"]*)\"$", (Integer idx, String item) -> assertThat(items.get(idx - 1), is(equalTo(item))));
        And("^the file \"([^\"]*)\" exists$", (String path) -> assertThat(String.format("File %s must exist", path), new File(path).exists()));
        When("^I complete todo item (\\d+)$", (Integer idx) -> getInjector().getInstance(DoneApi.class).done(idx));
        And("^the contents of file \"([^\"]*)\" starts with \"([^\"]*)\" together with today's date$", (String path, String startsWith) -> {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            startsWith += sdf.format(new Date());
            assertThat(content, startsWithIgnoringCase(startsWith));
        });
        When("^I complete todo item (\\d+) (\\d+) (\\d+) (\\d+)$", (Integer arg0, Integer arg1, Integer arg2, Integer arg3) -> {
            List<Integer> args = Arrays.asList(arg0, arg1, arg2, arg3);
            getInjector().getInstance(DoneApi.class).done(args);
        });
        And("^the contents of file \"([^\"]*)\" contains (\\d+) lines$", (String path, Integer num) -> {
            int count = 0;
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNext()) {
                if (!scanner.nextLine().isEmpty()) {
                    count++;
                }
            }
            assertThat(count, equalTo(num));
        });
        When("^I list the priority tasks$", () -> items = getInjector().getInstance(ListApi.class).listPriority());
        When("^I list the contexts$", () -> items = getInjector().getInstance(ListApi.class).listContexts());
        When("^I list the projects$", () -> items = getInjector().getInstance(ListApi.class).listProjects());
        When("^I list everything$", () -> items = getInjector().getInstance(ListApi.class).listAll());
        When("^I remove item (\\d+) (\\d+) (\\d+) (\\d+)$", (Integer arg0, Integer arg1, Integer arg2, Integer arg3) -> {
            List<Integer> args = Arrays.asList(arg0, arg1, arg2, arg3);
            getInjector().getInstance(RemoveApi.class).remove(args);
        });
        When("^I replace item (\\d+) with \"([^\"]*)\"$", (Integer idx, String todoItem) -> getInjector().getInstance(ReplaceApi.class).replace(idx, todoItem));
        And("^item (\\d+) will contain \"([^\"]*)\"$", (Integer idx, String todoItem) -> assertThat(items.get(idx - 1), containsString(todoItem)));
        When("^I add priority \"([^\"]*)\" to item (\\d+)$", (String priority, Integer idx) -> getInjector().getInstance(PriorityApi.class).add(idx, PriorityInput.valueOf(priority)));
        When("^I remove priority from item (\\d+)$", (Integer idx) -> getInjector().getInstance(PriorityApi.class).remove(idx));
    }

    private Injector injector;
    private TodoTxtStepDefsModule module;

    private Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(getModule());
        }
        return injector;
    }

    private TodoTxtStepDefsModule getModule() {
        if (module == null) {
            module = new TodoTxtStepDefsModule();
        }

        return module;
    }

    private class TodoTxtStepDefsModule extends AbstractModule {
        String name;

        @Override
        protected void configure() {
            bind(AddApi.class).to(AddTodoTxt.class);
            bind(ReplaceApi.class).to(ReplaceTodoTxt.class);
            bind(ListApi.class).to(ListTodo.class);
            bind(DoneApi.class).to(DoneTodoTxt.class);
            bind(RemoveApi.class).to(RemoveTodoTxt.class);
            bind(PriorityApi.class).to(PriorityTodoTxt.class);
            bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
        }

        @Provides
        File trackedFile() {
            return new File(name);
        }
    }
}
