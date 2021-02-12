package net.avdw.todo.extension.state;

import com.google.inject.Singleton;
import picocli.CommandLine.Option;

@Singleton
public class StateMixin {
    @Option(names = "--state", descriptionKey = "state.desc")
    boolean showState = false;
}
