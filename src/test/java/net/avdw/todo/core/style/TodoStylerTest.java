package net.avdw.todo.core.style;

import net.avdw.property.PropertyFile;
import net.avdw.todo.TestConstant;
import net.avdw.todo.color.ColorConverter;
import net.avdw.todo.core.mixin.CleanMixin;
import net.avdw.todo.core.style.parser.DateKeyParser;
import net.avdw.todo.core.style.parser.PropertyParser;
import net.avdw.todo.domain.Todo;
import net.avdw.todo.domain.TodoFileTypeBuilder;
import net.avdw.todo.domain.TodoTextCleaner;
import net.avdw.todo.repository.Any;
import net.avdw.todo.repository.FileRepository;
import net.avdw.todo.repository.Repository;
import org.junit.Test;
import org.tinylog.Logger;

import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;

public class TodoStylerTest {
    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void test() {
        Properties properties = new PropertyFile("net.avdw/todo").read("style");
        ColorConverter colorConverter = new ColorConverter();
        CleanMixin cleanMixin = new CleanMixin(new TodoTextCleaner());
        TodoStyler todoStyler = new TodoStyler(properties, new PropertyParser(properties, colorConverter, new DateKeyParser()), cleanMixin);
        Repository<Integer, Todo> repository = new FileRepository<>(Paths.get("src/test/resources/.todo/todo.txt"), new TodoFileTypeBuilder());
        repository.findAll(new Any<>()).forEach(todo -> {
            String styled = todoStyler.style(todo);
            assertNotNull("Styler is non-nullable", styled);
            Logger.debug(styled);
        });

    }
}
