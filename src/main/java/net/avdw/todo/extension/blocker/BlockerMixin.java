package net.avdw.todo.extension.blocker;

import net.avdw.todo.domain.Todo;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.Specification;
import picocli.CommandLine.Option;

public class BlockerMixin {
    @Option(names = "--blocker", descriptionKey = "blocker.cli.desc")
    private boolean listBlockers = false;

    public Specification<Integer, Todo> specification() {
        if (listBlockers) {
            return new BlockerExt();
        } else {
            return new Any<>();
        }
    }
}
