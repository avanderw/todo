package net.avdw.todo;

import cucumber.api.java8.En;
import net.avdw.todo.add.AddFunc;
import net.avdw.todo.done.DoneCli;
import net.avdw.todo.done.DoneFunc;
import net.avdw.todo.list.ListFunc;
import net.avdw.todo.priority.PriorityFunc;
import net.avdw.todo.remove.RemoveFunc;
import net.avdw.todo.replace.ReplaceFunc;
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
        Given("^I track the file \"([^\"]*)\"$", (String name) -> file = new File(name));
        When("^I list the todo items with no arguments$", () -> items = new ListFunc(file).list());
        Then("^I will get a list with (\\d+) items$", (Integer num) -> assertThat(items.size(), is(equalTo(num))));
        When("^I list the todo items with arguments \"([^\"]*)\"$", (String args) -> items = new ListFunc(file).list(Arrays.asList(args.split(" "))));
        Given("^the file \"([^\"]*)\" does not exist$", (String path) -> {
            if (!new File(path).delete()) {
                Logger.debug(String.format("%s removed", path));
            }
        });
        Given("^I copy the file \"([^\"]*)\" to \"([^\"]*)\"$", (String from, String to) -> Files.copy(Paths.get(from), Paths.get(to)));
        When("^I add a todo item$", () -> new AddFunc(file).add(UUID.randomUUID().toString()));
        And("^the last item will have a created date of now$", () -> assertThat("Must start with today's date", items.get(items.size() - 1).startsWith(sdf.format(new Date()), 5)));
        When("^I remove item (\\d+)$", (Integer idx) -> new RemoveFunc(file).remove(idx));
        And("^item (\\d+) will be \"([^\"]*)\"$", (Integer idx, String item) -> assertThat(items.get(idx - 1), is(equalTo(item))));
        And("^the file \"([^\"]*)\" exists$", (String path) -> assertThat(String.format("File %s must exist", path), new File(path).exists()));
        When("^I complete todo item (\\d+)$", (Integer idx) -> new DoneFunc(file).done(idx));
        And("^the contents of file \"([^\"]*)\" starts with \"([^\"]*)\" together with today's date$", (String path, String startsWith) -> {
            String content = new String(Files.readAllBytes(Paths.get(path)));
            startsWith += sdf.format(new Date());
            assertThat(content, startsWithIgnoringCase(startsWith));
        });
        When("^I complete todo item (\\d+) (\\d+) (\\d+) (\\d+)$", (Integer arg0, Integer arg1, Integer arg2, Integer arg3) -> {
            List<Integer> args = Arrays.asList(arg0, arg1, arg2, arg3);
            new DoneFunc(file).done(args);
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
        When("^I list the priority tasks$", () -> items = new ListFunc(file).listPriority());
        When("^I list the contexts$", () -> items = new ListFunc(file).listContexts());
        When("^I list the projects$", () -> items = new ListFunc(file).listProjects());
        When("^I list everything$", () -> items = new ListFunc(file).listAll());
        When("^I remove item (\\d+) (\\d+) (\\d+) (\\d+)$", (Integer arg0, Integer arg1, Integer arg2, Integer arg3) -> {
            List<Integer> args = Arrays.asList(arg0, arg1, arg2, arg3);
            new RemoveFunc(file).remove(args);
        });
        When("^I replace item (\\d+) with \"([^\"]*)\"$", (Integer idx, String todoItem) -> new ReplaceFunc(file).replace(idx, todoItem));
        And("^item (\\d+) will contain \"([^\"]*)\"$", (Integer idx, String todoItem) -> assertThat(items.get(idx -1), containsString(todoItem)));
        When("^I add priority \"([^\"]*)\" to item (\\d+)$", (String priority, Integer idx) -> new PriorityFunc(file).add(idx, priority));
        When("^I remove priority from item (\\d+)$", (Integer idx) -> new PriorityFunc(file).remove(idx));
    }
}
