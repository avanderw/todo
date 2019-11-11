package net.avdw.todo;

import com.google.inject.Singleton;

import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class RunningStats {
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    private long startedMs;
    private long finishedMs;

    public String getStarted() {
        return sdf.format(new Date(startedMs));
    }

    public String getFinished() {
        return sdf.format(new Date(finishedMs));
    }

    public String getDuration() {
        return String.format("%,dms", finishedMs - startedMs);
    }

    public void start() {
        startedMs = System.currentTimeMillis();
    }

    public void finish() {
        finishedMs = System.currentTimeMillis();
    }
}
