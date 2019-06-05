package net.avdw.todo.stepdefs;

import com.google.inject.Key;
import cucumber.api.java8.En;
import net.avdw.todo.CucumberCtx;
import net.avdw.todo.property.AProperty;
import net.avdw.todo.tracking.TrackApi;
import net.avdw.todo.tracking.TrackedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TrackingSteps implements En {
    public TrackingSteps(CucumberCtx cucumberCtx) {
        When("^I track the list \"([^\"]*)\"$", (String name) -> {
            cucumberCtx.getInstance(TrackApi.class).track(name);
        });
        Then("^my tracked list is \"([^\"]*)\"$", (String name) -> {
            assertThat(cucumberCtx.getInstance(Key.get(AProperty.class, TrackedList.class)).get(), is(equalTo(name)));
        });
    }
}
