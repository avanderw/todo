package net.avdw.todo.stepdefs;

import cucumber.api.java8.En;
import net.avdw.todo.CucumberCtx;
import net.avdw.todo.add.AddApi;

import java.util.UUID;

public class AddSteps implements En {
    public AddSteps(CucumberCtx cucumberCtx) {
        When("^I add a todo item$", () -> cucumberCtx.getInstance(AddApi.class).add(UUID.randomUUID().toString()));
    }
}
