package net.avdw.todo.extension.timing;

import com.google.inject.Singleton;
import picocli.CommandLine.Option;

@Singleton
public class TimingMixin {
    @Option(names = "--timing-detail", descriptionKey = "statistic.detail.desc")
    boolean showDetail = false;
}
