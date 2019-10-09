package net.avdw.todo;

// http://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html#16-colors
public final class Ansi {
    private Ansi() {
    }

    public static final String BLACK = "\u001b[30;1m";
    public static final String RED = "\u001b[31;1m";
    public static final String GREEN = "\u001b[32;1m";
    public static final String YELLOW = "\u001b[33m";
    public static final String BLUE = "\u001b[34;1m";
    public static final String MAGENTA = "\u001b[35;1m";
    public static final String CYAN = "\u001b[36m";
    public static final String WHITE = "\u001b[37;1m";
    public static final String RESET = "\u001b[0m";

    public static final String PROJECT_COLOR = MAGENTA;
    public static final String CONTEXT_COLOR = CYAN;

    public static void main(final String[] args) {
        for (int r = 0; r < 256; r++) {
            if (r % 32 == 0) {
                System.out.println();
            }
            System.out.print(String.format("\u001b[38;2;%s;0;0m %3s \u001b[0m", r, r));
        }
        System.out.println();
        for (int g = 0; g < 256; g++) {
            if (g % 32 == 0) {
                System.out.println();
            }
            System.out.print(String.format("\u001b[38;2;0;%s;0m %3s \u001b[0m", g, g));
        }
        System.out.println();
        for (int b = 0; b < 256; b++) {
            if (b % 32 == 0) {
                System.out.println();
            }
            System.out.print(String.format("\u001b[38;2;0;0;%sm %3s \u001b[0m", b, b));
        }
        System.out.println();
    }
}
