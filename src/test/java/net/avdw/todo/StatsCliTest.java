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

public class StatsCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/stats/.todo/todo.txt");
    private static CliTester cliTester;

    @AfterClass
    public static void afterClass() {
        cleanup(todoPath);
    }

    @BeforeClass
    public static void beforeClass() {
        setup(todoPath);
        cliTester = new CliTester(MainCli.class, new TestModule(todoPath));
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("src/test/resources/.todo/done.txt"), todoPath.getParent().resolve("done.txt"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = 256)
    public void testBasic() {
        cliTester.execute("do 72,70,55").success();
        cliTester.execute("stats").success()
                .contains("Reaction").contains("Cycle").contains("Lead")
                .contains("Max reaction time")
                .contains("Max cycle time")
                .contains("Max lead time")
                .contains("greater than mean + stddev reaction time")
                .contains("greater than mean + stddev cycle time")
                .contains("greater than mean + stddev lead time");
    }

    @Test(timeout = 256)
    public void testDone() {
        cliTester.execute("stats --incl-done").success().notContains("(n) = 1 todos");
    }

    @Test(timeout = 256)
    public void testBeforeAfterChange() {
        cliTester.execute("stats --changed-before 2019-12-31 --changed-after 2019-12-03").success();
    }

    @Test(timeout = 256)
    public void testBeforeChange() {
        cliTester.execute("stats --changed-before 2019-12-31").success();
    }

    @Test(timeout = 256)
    public void testBeforeDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("stats --done-before %s", now)).success().notContains("221 days");
    }

    @Test(timeout = 256)
    public void testBeforeTag() {
        cliTester.execute("stats --before-tag started:2020-01-01").success();
    }

    @Test(timeout = 256)
    public void testClean() {
        cliTester.execute("pri 1").success();
        cliTester.execute("do 2").success();
        cliTester.execute("stats --clean").success()
                .notContains("importance:").notContains("(A) ").notContains("2020-03-10").notContains("x 2")
                .notContains("@iBank").notContains("+Live_Better").notContains("iBankAdmin").notContains("Funeral_Cover")
                .contains("avanderwgmail.com").notContains("p 2018-11-14").notContains("r 2019-08-19");
    }

    @Test(timeout = 256)
    public void testContext() {
        cliTester.execute("stats @USSD").success();
    }

    @Test(timeout = 256)
    public void testNotFilter() {
        cliTester.execute("stats --not service --not refactor").success()
                .notContains("service").notContains("Refactor");
        cliTester.execute("stats --not service,refactor").success()
                .notContains("service").notContains("Refactor");
    }

    @Test(timeout = 256)
    public void testOrFilter() {
        cliTester.execute("stats service,refactor --or relationship --or enforcer").success();
    }

    @Test(timeout = 256)
    public void testProject() {
        cliTester.execute("stats +Live_Better").success();
    }

    @Test(timeout = 256)
    public void testTag() {
        cliTester.execute("stats urgency:5").success();
    }

}