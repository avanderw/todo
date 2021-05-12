package net.avdw.todo.extension;

import lombok.SneakyThrows;
import net.avdw.todo.CliTester;
import net.avdw.todo.TestConstant;
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

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

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
        cliTester = new CliTester(todoPath);
        warmup(cliTester);
    }

    @Before
    @SneakyThrows
    public void beforeTest() {
        Files.copy(Paths.get("src/test/resources/.todo/todo.txt"), todoPath, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(Paths.get("src/test/resources/.todo/done.txt"), todoPath.getParent().resolve("done.txt"), StandardCopyOption.REPLACE_EXISTING);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBasic() {
        cliTester.execute("do 72,70,55").success();
        cliTester.execute("stats").success()
                .contains("Reaction").contains("Cycle").contains("Lead")
                .contains("Max reaction time")
                .contains("Max cycle time")
                .contains("Max lead time")
                .contains("greater than mean + stddev reaction time")
                .contains("greater than mean + stddev lead time");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testDone() {
        cliTester.execute("stats --incl-done").success().notContains("(n) = 1 todos");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("stats --before done:%s", now)).success().notContains("221 days");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeTag() {
        cliTester.execute("stats --before started:2020-01-01").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testClean() {
        cliTester.execute("pri 1").success();
        cliTester.execute("do 2").success();
        cliTester.execute("stats --clean").success()
                .notContains("importance:").notContains("(A) ").notContains("2020-03-10").notContains("x 2")
                .notContains("@iBank").notContains("+Live_Better").notContains("iBankAdmin").notContains("Funeral_Cover")
                .contains("avanderwgmail.com").notContains("p 2018-11-14").notContains("r 2019-08-19");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testContext() {
        cliTester.execute("stats --and @USSD").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNotFilter() {
        cliTester.execute("stats --not service --not refactor").success()
                .notContains("service").notContains("Refactor");
        cliTester.execute("stats --not service,refactor").success()
                .notContains("service").notContains("Refactor");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testOrFilter() {
        cliTester.execute("stats --and service,refactor --or relationship --or enforcer").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testProject() {
        cliTester.execute("stats --and +Live_Better").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testTag() {
        cliTester.execute("stats --and urgency:5").success();
    }

}