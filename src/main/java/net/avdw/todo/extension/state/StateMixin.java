package net.avdw.todo.extension.state;

import picocli.CommandLine.Option;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StateMixin {
    @Option(names = "--state", descriptionKey = "state.desc")
    boolean showState = false;

    @Inject
    StateMixin() {
    }
}
