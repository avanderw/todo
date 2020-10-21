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

import static net.avdw.todo.TodoCliTestBootstrapper.cleanup;
import static net.avdw.todo.TodoCliTestBootstrapper.setup;
import static net.avdw.todo.TodoCliTestBootstrapper.warmup;

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
    public void testAfterAdd() {
        cliTester.execute("ls --added-after 2020-03-10").success().count("\\[", 2);
    }

    @Test(timeout = 256)
    public void testAfterChange() {
        cliTester.execute("ls --changed-after 2019-12-31").success().count("\\[", 34);
    }

    @Test(timeout = 256)
    public void testAfterDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("ls --done-after %s", now)).success().count("\\[", 0);
        cliTester.execute("do 5").success();
        cliTester.execute(String.format("ls --done-after %s", now)).success().count("\\[", 1);
    }

    @Test(timeout = 256)
    public void testAfterTag() {
        cliTester.execute("ls --after-tag started:2020-03-01").success().count("\\[", 9);
    }

    @Test(timeout = 256)
    public void testAfterTagFailure() {
        cliTester.execute("ls --after-tag start:20200310").failure();
        cliTester.execute("ls --after-tag start:").failure();
    }

    @Test(timeout = 256)
    public void testAnalytics() {
        cliTester.execute("ls").success().contains("ms");
    }

    @Test(timeout = 256)
    public void testAndOrNotFilter() {
        cliTester.execute("ls --and service,refactor --or relationship,enforcer --not card").success().count("\\[", 7);
    }

    @Test(timeout = 256)
    public void testBasic() {
        cliTester.execute("pri 1");
        cliTester.execute("ls").success().contains("[  1] (A)").count("\\[", 72)
                .contains("@iBank").contains("Last change");
    }

    @Test(timeout = 256)
    public void testBasicFilter() {
        cliTester.execute("ls --and service,refactor").success().notContains("[  1]").count("\\[", 6);
    }

    @Test(timeout = 256)
    public void testBeforeAdd() {
        cliTester.execute("ls --added-before 2019-03-01").success().count("\\[", 7);
    }

    @Test(timeout = 256)
    public void testBeforeAfterChange() {
        cliTester.execute("ls --changed-before 2019-12-31 --changed-after 2019-12-03").success().count("\\[", 3);
    }

    @Test(timeout = 256)
    public void testBlocker() {
        cliTester.execute("ls --blocker").success().count("\\[", 1);
    }

    @Test(timeout = 256)
    public void testBeforeChange() {
        cliTester.execute("ls --changed-before 2019-12-31").success().count("\\[", 37);
    }

    @Test(timeout = 256)
    public void testBeforeDone() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        String now = SIMPLE_DATE_FORMAT.format(gregorianCalendar.getTime());
        cliTester.execute(String.format("ls --done-before %s", now)).success().count("\\[", 2);
        cliTester.execute("do 2").success();
        cliTester.execute(String.format("ls --done-before %s", now)).success().count("\\[", 3);
    }

    @Test(timeout = 256)
    public void testBeforeTag() {
        cliTester.execute("ls --before-tag started:2020-01-01").success().count("\\[", 5);
    }

    @Test(timeout = 256)
    public void testBeforeTagFailure() {
        cliTester.execute("ls --before-tag start:20200310").failure();
        cliTester.execute("ls --before-tag start:").failure();
    }

    @Test(timeout = 256)
    public void testChangeDetail() {
        cliTester.execute("ls --change-detail").success().contains("months ago").contains("n/a");
    }

    @Test(timeout = 256)
    public void testClean() {
        cliTester.execute("pri 1").success();
        cliTester.execute("ls --clean").success()
                .notContains("importance:").notContains("(A) ").notContains("2020-03-10").notContains("x 2")
                .notContains("@iBank").notContains("+Live_Better").notContains("iBankAdmin").notContains("Funeral_Cover")
                .contains("avanderwgmail.com").notContains("p 2018-11-14").notContains("r 2019-08-19");
    }

    @Test(timeout = 256)
    public void testCount() {
        cliTester.execute("ls").success().contains("72 of 72");
    }

    @Test(timeout = 256)
    public void testDone() {
        cliTester.execute("ls --incl-done").success().contains("[ 73] x").count("\\[", 80);
    }

    @Test(timeout = 256)
    public void testProgress() {
        cliTester.execute("ls --incl-done").success()
                .contains("15% completion").contains("1 parked").contains("1 removed")
                .notContains("0% completion");
    }

    @Test(timeout = 256)
    public void testGroupByContext() {
        cliTester.execute("ls --group-by @").success()
                .contains("CNP, Track1 Context")
                .notContains(" gmail.com")
                .count("\\s## ", 16);
    }

    @Test(timeout = 256)
    public void testGroupByContextSpecific() {
        cliTester.execute("ls --and @iBank --or @Track1 --group-by @").success().count("\\s## ", 6);
    }

    @Test(timeout = 256)
    public void testGroupByDeepHierarchy() {
        cliTester.execute("ls --group-by +,@,assigned:,importance:").failure();
    }

    @Test(timeout = 256)
    public void testGroupByProject() {
        cliTester.execute("ls --group-by +").success().count("\\s## ", 6);
    }

    @Test(timeout = 256)
    public void testGroupByProjectSpecific() {
        cliTester.execute("ls --and +ROB --or +Access_Facility --group-by +").success().count("\\s## ", 2);
    }

    @Test(timeout = 256)
    public void testGroupByTag() {
        cliTester.execute("ls --group-by urgency:").success().count("\\s## ", 8);
    }

    @Test(timeout = 256)
    public void testGroupByThreeHierarchy() {
        cliTester.execute("ls --group-by +,@,assigned:").success().count("\\s#### ", 33);
    }

    @Test(timeout = 256)
    public void testGroupByTwoHierarchy() {
        cliTester.execute("ls --group-by urgency:,importance:").success().count("\\s### ", 31);
    }

    @Test(timeout = 256)
    public void testHelp() {
        cliTester.execute("ls --help").success();
    }

    @Test(timeout = 256)
    public void testNotFilter() {
        cliTester.execute("ls --not service --not refactor").success().notContains("service").notContains("Refactor");
        cliTester.execute("ls --not service,refactor").success().notContains("service").notContains("Refactor");
    }

    @Test(timeout = 256)
    public void testOrFilter() {
        cliTester.execute("ls --and service,refactor --or relationship --or enforcer").success().notContains("[  1]").count("\\[", 8);
        cliTester.execute("ls --and service,refactor --or relationship,enforcer").success();
    }

    @Test(timeout = 256)
    public void testParked() {
        cliTester.execute("archive").success();
        cliTester.execute("ls --incl-parked").success().count("\\[", 70);
    }

    @Test(timeout = 256)
    public void testRemoved() {
        cliTester.execute("archive").success();
        cliTester.execute("ls --incl-removed").success().count("\\[", 69);
    }

    @Test(timeout = 256)
    public void testStatistic() {
        cliTester.execute("ls --incl-done").success()
                .contains("Reaction").contains("Cycle").contains("Lead");
    }

    @Test(timeout = 256)
    public void testStatisticDetail() {
        cliTester.execute("ls --incl-done --detail").success()
                .contains("Reaction").contains("Cycle").contains("Lead")
                .contains("(Q1)=").contains("(Q2)=").contains("(Q3)=");
    }
}
