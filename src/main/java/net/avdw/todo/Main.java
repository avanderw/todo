package net.avdw.todo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import picocli.CommandLine;

import java.io.PrintWriter;

public final class Main {
    private Main() {
    }

    public static void main(final String[] args) {
        Injector injector = Guice.createInjector(new MainModule());
        injector.getInstance(CommandLineBuilder.class).build(MainCli.class, injector).execute(args);
    }

}
