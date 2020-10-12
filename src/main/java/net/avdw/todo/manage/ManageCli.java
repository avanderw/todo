package net.avdw.todo.manage;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "manage", resourceBundle = "messages", description = "${bundle:manage}",
        mixinStandardHelpOptions = true,
        subcommands = {ValueCli.class})
public class ManageCli implements Runnable {
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
        spec.commandLine().usage(spec.commandLine().getOut());
    }
}
