package net.avdw.update.adapter.in;

import net.avdw.update.port.in.UpdateAvailableQuery;
import net.avdw.update.port.in.UpdateToLatestUseCase;
import org.tinylog.Logger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import javax.inject.Inject;

@Command(name = "update", description = "Update the application.",
        mixinStandardHelpOptions = true,
        hidden = true)
public class UpdateCliAdapter implements Runnable {
    private final UpdateAvailableQuery updateAvailableQuery;
    private final UpdateToLatestUseCase updateToLatestUseCase;

    @Option(names = {"-f", "--force"}, description = "Force the update.")
    private boolean force;

    @Inject
    UpdateCliAdapter(final UpdateAvailableQuery updateAvailableQuery, final UpdateToLatestUseCase updateToLatestUseCase) {
        this.updateAvailableQuery = updateAvailableQuery;
        this.updateToLatestUseCase = updateToLatestUseCase;
    }

    @Override
    public void run() {
        if (force) {
            System.out.println("Forcing update to the latest version.");
            try {
                updateToLatestUseCase.runUpdate();
            } catch (UnsupportedOperationException e) {
                System.out.println(e.getMessage());
                Logger.debug(e);
            }
        } else if (updateAvailableQuery.isUpdateAvailable()) {
            System.out.println("Update is available. Downloading update.");
            updateToLatestUseCase.runUpdate();
            System.out.println("Applying update.");
        } else {
            System.out.println("No update is available.\n" +
                    "  (use '--force' to ignore and install the latest version)");
        }
    }
}
