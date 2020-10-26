package net.avdw.todo.style.painter;

import org.junit.Test;

import static org.junit.Assert.*;

public class RegexPainterTest {

    @Test
    public void paint() {
        String str = "x 2020-10-21 2020-10-21 Combine changelog with list using the group-by option";
        RegexPainter regexPainter = new RegexPainter("[\\d-]{10}", "<add>");

        String expected = "x <add>2020-10-21</add> <add>2020-10-21</add> Combine changelog with list using the group-by option";
        assertEquals(expected, regexPainter.paint(str, "</add>"));
    }
}