package net.avdw.todo.repository.wunderlist;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import cucumber.api.java8.En;
import net.avdw.todo.LoggingModule;
import net.avdw.todo.repository.wunderlist.client.v1.AList;
import net.avdw.todo.repository.wunderlist.client.v1.ListEndpoint;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ConnectFeatureSteps implements En {
    private List<AList> lists;
    private final WunderlistConfig wunderlistConfig = new WunderlistConfig();

    public ConnectFeatureSteps() {

        Given("^an API Key \"([^\"]*)\"$", wunderlistConfig::setApiKey);
        And("^an API Secret \"([^\"]*)\"$", wunderlistConfig::setApiSecret);
        And("^a Client Key \"([^\"]*)\"$", wunderlistConfig::setClientKey);
        When("^I retrieve the lists for the client$", () -> lists = getInjector().getInstance(ListEndpoint.class).getLists());
        Then("^there is at least one filtering retrieved$", () -> assertThat(lists, is(not(empty()))));
    }

    private Injector injector;
    private Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(new Config());
        }
        return injector;
    }

    class Config extends AbstractModule {
        @Override
        protected void configure() {
            install(new LoggingModule());
            install(wunderlistConfig);
        }
    }
}
