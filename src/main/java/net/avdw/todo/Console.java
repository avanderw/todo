package net.avdw.todo;

public final class Console {
    private Console() {
    }

    public static void h1(final String text) {
        System.out.println(String.format("--- %sTODO: %s%s ---", Ansi.GREEN, text, Ansi.RESET));
    }

    public static void info(final String text) {
        System.out.println(String.format("%s", text));
    }

    public static void divide() {
        System.out.println("---");
    }

    public static void error(final String text) {
        System.out.println(String.format("! %s", text));
    }

    public static void blank() {
        System.out.println();
    }
}
