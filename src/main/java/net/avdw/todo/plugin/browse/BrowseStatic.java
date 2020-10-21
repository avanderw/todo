package net.avdw.todo.plugin.browse;

import java.util.regex.Pattern;

public class BrowseStatic {
    public static final Pattern PATTERN = Pattern.compile("(?<protocol>https?|tel|telnet|mailto):\\S+\\s?");

    private BrowseStatic() {
    }
}
