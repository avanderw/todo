package net.avdw.todo.list;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import cucumber.api.java8.En;
import net.avdw.todo.property.AProperty;
import net.avdw.todo.list.tracking.TrackApi;
import net.avdw.todo.list.tracking.TrackedList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TrackingStepdefs implements En {
    public TrackingStepdefs() {
        final Injector injector = Guice.createInjector(new Config());
        When("^I track the filtering \"([^\"]*)\"$", (String name) -> injector.getInstance(TrackApi.class).track(name));
        Then("^my tracked filtering is \"([^\"]*)\"$", (String name) ->
                assertThat(injector.getInstance(Key.get(AProperty.class, TrackedList.class)).get(), is(equalTo(name))));
    }

    class Config extends AbstractModule {
        @Override
        protected void configure() {
            //install(new PropertyModule());
//            install(new TrackingModule());
        }
    }
}
