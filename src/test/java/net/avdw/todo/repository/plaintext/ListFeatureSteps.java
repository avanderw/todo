package net.avdw.todo.repository.plaintext;

import cucumber.api.java8.En;
import net.avdw.todo.CucumberCtx;
import net.avdw.todo.list.filtering.ListApi;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ListFeatureSteps implements En {
    private List<String> cacheList;
    public ListFeatureSteps(CucumberCtx cucumberCtx) {
        When("^I filtering the todo items with no arguments$", () -> cacheList = cucumberCtx.getInstance(ListApi.class).list());
        Then("^I will get a filtering with (\\d+) items$", (Integer size) -> assertThat(cacheList.size(), is(equalTo(size))));
    }
}
