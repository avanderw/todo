package net.avdw.todo.repository.file;

import cucumber.api.java8.En;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ListFeatureSteps implements En {
    private List<String> cacheList;
    public ListFeatureSteps() {
//        When("^I filtering the todo items with no arguments$", () -> cacheList = cucumberCtx.getInstance(ListApi.class).list());
        Then("^I will get a filtering with (\\d+) items$", (Integer size) -> assertThat(cacheList.size(), is(equalTo(size))));
    }
}