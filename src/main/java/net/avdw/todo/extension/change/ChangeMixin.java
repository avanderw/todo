package net.avdw.todo.extension.change;

import picocli.CommandLine.Option;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ChangeMixin {
    @Option(names = "--change-detail", descriptionKey = "change.mixin.desc.change.detail")
    boolean showDetail = false;

    @Inject
    ChangeMixin() {
    }
}
