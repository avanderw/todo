package net.avdw.todo.tracking;

import com.google.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(name = "track", description = "Track a list.")
public class TrackCli implements Runnable {
    @CommandLine.Parameters(description = "The list to tracking.")
    private String list;

    @Inject
    private TrackApi trackApi;

    @Override
    public void run() {
        trackApi.track(list);
    }
}
