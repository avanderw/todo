package net.avdw.todo.extension.change;

import com.google.inject.Singleton;
import picocli.CommandLine.Option;

@Singleton
public class ChangeMixin {
    @Option(names = "--change-detail", descriptionKey = "change.mixin.desc.change.detail")
    boolean showDetail = false;
}
