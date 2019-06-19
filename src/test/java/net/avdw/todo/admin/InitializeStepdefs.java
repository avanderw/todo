package net.avdw.todo.admin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import cucumber.api.java8.En;
import net.avdw.todo.admin.initialize.AInitializer;
import net.avdw.todo.admin.initialize.InitializeModule;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;

public class InitializeStepdefs implements En {
    public InitializeStepdefs() {

        final Injector injector = Guice.createInjector(new InitializeModule());

        When("^a default todo is initialized$", () -> injector.getInstance(AInitializer.class).init());
        When("^a the target \"([^\"]*)\" is initialized$", (String target) -> injector.getInstance(AInitializer.class).init(Paths.get(target)));
        Then("^the target \"([^\"]*)\" will contain the folder \"([^\"]*)\"$", (String target, String folder) -> {
            Path targetPath = Paths.get(target);
            assertThat("Todo directory is created", Files.isDirectory(targetPath.resolve(folder)));
        });
    }
}
