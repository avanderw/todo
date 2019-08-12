package net.avdw.todo;

public class Console {
    private Console(){}

    public static void h1(String text) {
        System.out.println(String.format("--- %sTODO: %s%s ---", Ansi.Green, text, Ansi.Reset));
    }
    public static void info(String text) {
        System.out.println(String.format("%s", text));
    }

    public static void divide() {
        System.out.println("---");
    }

    public static void error(String text) {
        System.out.println(String.format("err %s", text));
    }

    public static void blank() {
        System.out.println();
    }
}
