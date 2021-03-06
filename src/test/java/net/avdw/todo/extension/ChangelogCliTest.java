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

public class ChangelogCliTest {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final Path todoPath = Paths.get("target/test-resources/changelog/.todo/todo.txt");
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
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAfterAdd() {
        cliTester.execute("changelog --after added:2020-03-10").success().count("\\[", 25);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAfterDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("changelog --after done:%s", now)).success().count("\\[", 0);
        cliTester.execute("do 5").success();
        cliTester.execute(String.format("changelog --after done:%s", now)).success().count("\\[", 2);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testAfterTag() {
        cliTester.execute("changelog --after started:2020-03-01").success().count("\\[", 25);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBasic() {
        cliTester.execute("changelog").success()
                .contains("July 2018").contains("October 2018")
                .count("\\[ 50\\] x 2020-05-27 2020-01-05 @CNP bugfixes and 30s failover assigned:shane end:2020-03-03 2020-02-24:releasing", 2);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testStarted() {
        cliTester.execute("changelog").success()
                .contains("Started");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeAdd() {
        cliTester.execute("changelog --before added:2019-03-01").success().count("\\[", 8);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeAfterAdd() {
        cliTester.execute("changelog --before added:2019-03-01 --after added:2018-12-31").success().count("\\[", 2);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("changelog --before done:%s", now)).success().count("\\[", 6);
        cliTester.execute("do 3").success();
        cliTester.execute(String.format("changelog --before done:%s", now)).success().count("\\[", 8);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testBeforeTag() {
        cliTester.execute("changelog --before started:2020-01-01").success().count("\\[", 10);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testClean() {
        cliTester.execute("pri 1").success();
        cliTester.execute("changelog --clean").success()
                .notContains("importance:").notContains("(A) ").notContains("2020-03-10").notContains("x 2")
                .notContains("@iBank").notContains("+Live_Better").notContains("iBankAdmin").notContains("Funeral_Cover")
                .contains("avanderwgmail.com").notContains("p 2018-11-14").notContains("r 2019-08-19");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testContext() {
        cliTester.execute("changelog --and @USSD").success()
                .contains("March 2019").contains("August 2019");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testNotFilter() {
        cliTester.execute("changelog --not service --not refactor").success()
                .contains("November 2018").contains("January 2019")
                .notContains("service").notContains("Refactor");
        cliTester.execute("changelog --not service,refactor").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testOrFilter() {
        cliTester.execute("changelog --and service,refactor --or relationship --or enforcer").success()
                .contains("October 2018").contains("May 2019")
                .count("\\[", 8);
        cliTester.execute("changelog --and service,refactor --or relationship,enforcer").success();
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testProject() {
        cliTester.execute("changelog --and +Live_Better").success()
                .contains("January 2020")
                .count("\\[", 2);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testTag() {
        cliTester.execute("changelog --and urgency:5").success().contains("July 2018").count("\\[", 11);
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testWeek() {
        cliTester.execute("changelog --by-week").success().notContains("2019/02");
    }

    @Test(timeout = TestConstant.PERFORMANCE_TIMEOUT)
    public void testYear() {
        cliTester.execute("changelog --by-year").success().notContains("2019/02");
    }

}
