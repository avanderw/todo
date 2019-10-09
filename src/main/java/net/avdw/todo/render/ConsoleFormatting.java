package net.avdw.todo.render;

import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;

public final class ConsoleFormatting {

    private static final int LINE_LENGTH = 80;

    private ConsoleFormatting() {
    }

    public static void h1(final String text) {
        Logger.info(StringUtils.center(String.format("< %s >", text), LINE_LENGTH, "-"));
    }

    public static void h2(final String text) {
        Logger.info(StringUtils.center(String.format("[ %s ]", text), LINE_LENGTH, "-"));
    }

    public static void h3(final String text) {
        Logger.info(text);
    }

    public static void hr() {
        Logger.info(StringUtils.repeat("-", LINE_LENGTH));
    }
}
