package net.avdw.todo;

import com.google.inject.Singleton;

import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class RunningStats {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private final long startedMs;

    RunningStats() {
        startedMs = System.currentTimeMillis();
    }

    public String getStarted() {
        return simpleDateFormat.format(new Date(startedMs));
    }

    public String getDuration() {
        return String.format("%,dms", System.currentTimeMillis() - startedMs);
    }
}
