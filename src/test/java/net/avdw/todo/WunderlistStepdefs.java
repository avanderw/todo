package net.avdw.todo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import cucumber.api.java8.En;
import net.avdw.todo.add.AddApi;
import net.avdw.todo.add.AddWunderlist;
import net.avdw.todo.wunderlist.IgnoreSsl;
import net.avdw.todo.wunderlist.WunderlistClient;
import net.avdw.todo.wunderlist.WunderlistModule;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class WunderlistStepdefs implements En {

    public WunderlistStepdefs() {
        Logger.getConfiguration()
                .formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}() {level}: {message}")
                .level(Level.TRACE).activate();
        Given("^there is no Wunderlist$", () -> {
            if (getInjector().getInstance(WunderlistClient.class).databaseExists()) {
                getInjector().getInstance(WunderlistClient.class).deleteDatabase();
            }
        });
        When("^I take an action on Wunderlist$", () -> {
            getInjector().getInstance(AddApi.class).add("Taking an action on Wunderlist");
        });
        Then("^the Wunderlist will be created$", () -> {
            assertThat(getInjector().getInstance(WunderlistClient.class).databaseExists(), is(equalTo(true)));
        });
    }


    private Injector injector;
    private Injector getInjector() {
        if (injector == null) {
            injector = Guice.createInjector(getModule());
        }
        return injector;
    }

    private WunderlistStepDefsModule module;
    private WunderlistStepDefsModule getModule() {
        if (module == null) {
            module = new WunderlistStepDefsModule();
        }

        return module;
    }

    private class WunderlistStepDefsModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(AddApi.class).to(AddWunderlist.class);
//            bind(ReplaceApi.class).to(ReplaceWunderlist.class);
//            bind(ListApi.class).to(ListTodo.class);
//            bind(DoneApi.class).to(DoneWunderlist.class);
//            bind(RemoveApi.class).to(RemoveWunderlist.class);
//            bind(PriorityApi.class).to(PriorityWunderlist.class);
            bind(SimpleDateFormat.class).toInstance(new SimpleDateFormat("yyyy-MM-dd"));
            bindInterceptor(Matchers.inSubpackage("net.avdw.todo"), Matchers.any(), new LoggingInterceptor());
            bind(IgnoreSsl.class).asEagerSingleton();
            bind(String.class).annotatedWith(Names.named("WUNDERLIST_NAME")).toInstance("todo.txt-test");
            install(new WunderlistModule());
        }
    }
}
