package net.avdw.todo.style;

import net.avdw.todo.PropertyFile;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import net.avdw.todo.style.parser.DateKeyParser;
import net.avdw.todo.style.parser.PropertyParser;
import org.junit.Test;
import org.tinylog.Logger;

import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class TodoStylerTest {
    @Test
    public void test() {
        Properties properties = new PropertyFile("net.avdw/todo").read("style-test");
        ColorConverter colorConverter = new ColorConverter();
        TodoStyler todoStyler = new TodoStyler(properties, new PropertyParser(properties, colorConverter, new DateKeyParser()));
        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        repository.findAll(new Any<>()).forEach(todo -> {
            String styled = todoStyler.style(todo);
            assertNotNull("Styler is non-nullable", styled);
            Logger.debug(styled);
        });

    }
}
