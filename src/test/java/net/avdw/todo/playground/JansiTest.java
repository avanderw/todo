package net.avdw.todo.playground;

import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class JansiTest {
    public static final String ANSI_AT55 = "\u001b[10;10H";
    public static final String ANSI_BOLD = "\u001b[1m";
    public static final String ANSI_CLS = "\u001b[2J";
    public static final String ANSI_HOME = "\u001b[H";
    public static final String ANSI_NORMAL = "\u001b[0m";
    public static final String ANSI_REVERSEON = "\u001b[7m";
    public static final String ANSI_WHITEONBLUE = "\u001b[37;44m";

    public static void main(String[] args) {
        System.out.println(Ansi.isDetected());
        System.out.println(Ansi.isEnabled());
        test1();
        test2();
    }

    public static void test1() {
        AnsiConsole.systemInstall();
        AnsiConsole.out.println(ANSI_CLS);
        AnsiConsole.out.println
                (ANSI_AT55 + ANSI_REVERSEON + "Hello world" + ANSI_NORMAL);
        AnsiConsole.out.println
                (ANSI_HOME + ANSI_WHITEONBLUE + "Hello world" + ANSI_NORMAL);
        AnsiConsole.out.print
                (ANSI_BOLD + "Press a key..." + ANSI_NORMAL);
        AnsiConsole.out.println(ANSI_CLS);
        AnsiConsole.systemInstall();
    }

    public static void test2() {
        AnsiConsole.systemInstall();
        Ansi.setEnabled(false);
        System.out.println(ansi().eraseScreen().render("@|red Hello|@ @|green World|@"));
        System.out.println(ansi().eraseScreen().render("@|red Hello|@ @|green World|@", 3));
        System.out.println(ansi().eraseScreen().fg(Ansi.Color.RED).a("Hello ").fg(Ansi.Color.GREEN).a("World").reset());
        ColorConverter converter = null;//new ColorConverter();
        System.out.println(Ansi.ansi().a(converter.hexToAnsiFg(0xFF0000)).a("Hello ").a(converter.hexToAnsiFg(0x00FF00)).a("World").reset().a(" Default"));
        AnsiConsole.systemUninstall();
    }
}
