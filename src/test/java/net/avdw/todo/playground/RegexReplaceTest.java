package net.avdw.todo.playground;

import net.avdw.todo.style.painter.RegexPainter;

public class RegexReplaceTest {
    public static void main(String[] args) {
        String str = "x 2020-10-21 2020-10-21 Combine changelog with list using the group-by option";
        System.out.println(str.replaceAll("([\\d-]+)", "<date>"));
        System.out.println(str.replaceAll("x ([\\d-]+)", "Complete on $1"));
        System.out.println(str.replaceAll("x ([\\d-]+)", "x <done>$1<done>").replaceAll("([xpr] \\S+ )([\\d-]+)", "$1<add>$2<add>"));

        RegexPainter regexPainter = new RegexPainter("([xpr] \\S+ )([\\d-]+)(.*)", "<add>");
        System.out.println(regexPainter.paint(str, "</add>"));

        str = "2020-03-10 +Funeral_Cover compliance analyst:Julias importance:13 urgency:21";
        System.out.println(str.replaceAll("(\\S+:\\S+)", "<a>$1</a>"));
    }
}
