package net.avdw.todo.extension.browse;

import java.util.regex.Pattern;

public final class BrowseStatic {
    public static final Pattern PATTERN = Pattern.compile("(?<protocol>https?|tel|telnet|mailto|file):\\S+\\s?");

    private BrowseStatic() {
    }
}
