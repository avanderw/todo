package net.avdw.todo.playground;

import net.avdw.todo.color.AnsiColorTest;
import net.avdw.todo.color.ColorConverter;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class JansiTest {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Ansi.setEnabled(false);
        System.out.println( ansi().eraseScreen().render("@|red Hello|@ @|green World|@") );
        System.out.println( ansi().eraseScreen().render("@|red Hello|@ @|green World|@", 3) );
        System.out.println( ansi().eraseScreen().fg(Ansi.Color.RED).a("Hello ").fg(Ansi.Color.GREEN).a("World").reset());
        ColorConverter converter = new ColorConverter();
        System.out.println(Ansi.ansi().a(converter.hexToAnsiFg(0xFF0000)).a("Hello ").a(converter.hexToAnsiFg(0x00FF00)).a("World").reset().a(" Default"));
        AnsiConsole.systemUninstall();
    }
}
