package net.avdw.todo.list.tracking;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "track", description = "Track a filtering.")
public class TrackCli implements Runnable {
    @CommandLine.Parameters(description = "The filtering to tracking.")
    private String list;

    @Inject
    private TrackApi trackApi;

    @Override
    public void run() {
        trackApi.track(list);
    }
}
