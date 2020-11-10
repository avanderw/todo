package net.avdw.todo.style.parser;

import net.avdw.todo.PropertyFile;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.style.painter.DatePainter;
import net.avdw.todo.style.painter.RegexPainter;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

public class PropertyParserTest {
    private final Properties properties = new PropertyFile("net.avdw/todo").read("style");
    private final ColorConverter colorConverter = new ColorConverter();
    private final PropertyParser parser = new PropertyParser(properties, colorConverter, new DateKeyParser());

    @Test
    public void date() {
        assertEquals(DatePainter.class, parser.parse("date.done.new.-1m+").orElseThrow().getClass());
    }


    @Test
    public void regex() {
        assertEquals(RegexPainter.class, parser.parse("regex.context").orElseThrow().getClass());
    }

    @Test
    public void color() {
        assertTrue("There is no painter for color properties", parser.parse("regex.id.color").isEmpty());
    }

}