package net.avdw.todo.manage;

import com.google.inject.Inject;
import net.avdw.todo.ResourceBundleKey;
import net.avdw.todo.TemplatedResourceBundle;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;

@Command(name = "value", resourceBundle = "messages", description = "${bundle:manage.value.title}",
        mixinStandardHelpOptions = true)
public class ValueCli implements Runnable {

    @Inject
    private TemplatedResourceBundle templatedResourceBundle;
    @Spec
    private CommandSpec spec;

    @Override
    public void run() {
    }
}
