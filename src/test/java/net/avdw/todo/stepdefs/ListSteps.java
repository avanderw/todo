package net.avdw.todo.stepdefs;

import cucumber.api.java8.En;
import net.avdw.todo.CucumberCtx;
import net.avdw.todo.list.ListApi;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ListSteps implements En {
    private List<String> cacheList;
    public ListSteps(CucumberCtx cucumberCtx) {
        When("^I list the todo items with no arguments$", () -> cacheList = cucumberCtx.getInstance(ListApi.class).list());
        Then("^I will get a list with (\\d+) items$", (Integer size) -> assertThat(cacheList.size(), is(equalTo(size))));
    }
}
