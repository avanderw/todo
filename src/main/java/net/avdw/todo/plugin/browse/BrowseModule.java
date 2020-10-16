package net.avdw.todo.plugin.browse;

import com.google.inject.AbstractModule;
import net.avdw.todo.plugin.Plugin;

import java.util.regex.Pattern;

@Plugin
public class BrowseModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Pattern.class)
                .annotatedWith(Browse.class)
                .toInstance(Pattern.compile("(?<protocol>https?|tel|telnet|mailto):\\S+\\s?"));
    }
}
