package net.avdw.todo;

import lombok.SneakyThrows;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static net.avdw.todo.TodoCliTestBootstrapper.*;
import static org.junit.Assert.fail;

public class ListCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/list/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(RefactoredMainCli.class, new TestGuiceFactory(new TestModule(todoPath)));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = 50)
    public void testAndOrNotFilter() {
        cliTester.execute("ls service refactor --or relationship,enforcer --not card").success().count("\\[", 7);
    }

    @Test(timeout = 150)
    public void testBasic() {
        cliTester.execute("pri 1");
        cliTester.execute("do 2");
        cliTester.execute("ls").success().startsWith("[  1]").count("\\[", 72).contains("@iBank");
    }

    @Test(timeout = 150)
    public void testBasicFilter() {
        cliTester.execute("ls service refactor").success().startsWith("[  6]").count("\\[", 6);
        cliTester.execute("ls service,refactor").success().startsWith("[  6]").count("\\[", 6);
    }

    @Test(timeout = 150)
    public void testDone() {
        cliTester.execute("do 1,2,3,4,5,6,7,8,9").success();
        cliTester.execute("archive").success();
        cliTester.execute("do 1").success();
        cliTester.execute("ls --done").success().startsWith("[  1]");
    }

    @Test(timeout = 150)
    public void testRemoved() {
        cliTester.execute("rm 1,2,3,4,5,6,7,8,9").success();
        cliTester.execute("archive").success();
        cliTester.execute("rm 1").success();
        cliTester.execute("ls --removed").success().startsWith("[  1]");
    }

    @Test(timeout = 150)
    public void testParked() {
        cliTester.execute("park 1,2,3,4,5,6,7,8,9").success();
        cliTester.execute("archive").success();
        cliTester.execute("park 1").success();
        cliTester.execute("ls --parked").success().startsWith("[  1]");
    }

    @Test(timeout = 100)
    public void testExclusive() {
        cliTester.execute("ls --done --parked --removed").failure();
    }

    @Test(timeout = 50)
    public void testAfterTag() {
        cliTester.execute("ls --after start:2020-03-01").success().count("\\[", 9);
    }

    @Test(timeout = 100)
    public void testAfterDone() {
        cliTester.execute("ls --done-after 2020-01-01").success().count("\\[", 0);
        cliTester.execute("do 5").success();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        cliTester.execute(String.format("ls --done-after %s", SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime()))).success().count("\\[", 1);
    }

    @Test(timeout = 50)
    public void testAfterAdd() {
        cliTester.execute("ls --added-after 2020-03-10").success().count("\\[", 3);
    }

    @Test(timeout = 50)
    public void testBeforeTag() {
        cliTester.execute("ls --before start:2020-01-01").success().count("\\[", 5);
    }

    @Test(timeout = 100)
    public void testBeforeDone() {
        cliTester.execute("ls --done-before 2020-01-01").success().count("\\[", 0);
        cliTester.execute("do 2").success();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        cliTester.execute(String.format("ls --done-before %s", SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime()))).success().count("\\[", 1);
    }

    @Test(timeout = 50)
    public void testBeforeAdd() {
        cliTester.execute("ls --added-before 2019-03-01").success().count("\\[", 8);
    }

    @Test(timeout = 150)
    public void testClean() {
        cliTester.execute("pri 1").success();
        cliTester.execute("do 2").success();
        cliTester.execute("ls --clean").success()
                .notContains("importance:").notContains("(A) ").notContains("2020-03-10").notContains("x 2")
                .notContains("@iBank").notContains("+Live_Better").notContains("iBankAdmin").notContains("Funeral_Cover")
                .contains("avanderwgmail.com");
    }

    @Test(timeout = 100)
    public void testNotFilter() {
        cliTester.execute("ls --not service --not refactor").success().notContains("service").notContains("Refactor");
        cliTester.execute("ls --not service,refactor").success().notContains("service").notContains("Refactor");
    }

    @Test(timeout = 100)
    public void testOrFilter() {
        cliTester.execute("ls service,refactor --or relationship --or enforcer").success().startsWith("[  5]").count("\\[", 8);
        cliTester.execute("ls service,refactor --or relationship,enforcer").success().startsWith("[  5]").count("\\[", 8);
    }

}
