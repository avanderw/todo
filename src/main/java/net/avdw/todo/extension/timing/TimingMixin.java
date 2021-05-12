package net.avdw.todo.extension.timing;

import picocli.CommandLine.Option;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TimingMixin {
    @Option(names = "--timing-detail", descriptionKey = "statistic.detail.desc")
    boolean showDetail = false;

    @Inject
    TimingMixin() {
    }
}
