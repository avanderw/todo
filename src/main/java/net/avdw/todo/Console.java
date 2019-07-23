package net.avdw.todo;

public class Console {
    private Console(){}

    public static void h1(String text) {
        System.out.println(String.format("h1 %s", text));
    }
    public static void info(String text) {
        System.out.println(String.format("i %s", text));
    }

    public static void divide() {
        System.out.println("--");
    }
}
