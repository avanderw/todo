package net.avdw.update;

import lombok.SneakyThrows;
import net.avdw.update.domain.Release;
import net.avdw.update.port.in.UpdateAvailableQuery;
import net.avdw.update.port.in.UpdateToLatestUseCase;
import net.avdw.update.port.out.CurrentReleasePort;
import net.avdw.update.port.out.DownloadPort;
import net.avdw.update.port.out.InstallPort;
import net.avdw.update.port.out.LatestReleasePort;
import net.avdw.update.port.out.UnzipPort;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;

class UpdateService implements UpdateAvailableQuery, UpdateToLatestUseCase {
    private final CurrentReleasePort currentReleasePort;
    private final LatestReleasePort latestReleasePort;
    private final DownloadPort downloadPort;
    private final UnzipPort unzipPort;
    private final InstallPort installPort;
    private final Path jarPath;

    @Inject
    UpdateService(final CurrentReleasePort currentReleasePort, final LatestReleasePort latestReleasePort, final DownloadPort downloadPort, final UnzipPort unzipPort, final InstallPort installPort, @UpdateFeature final Path jarPath) {
        this.currentReleasePort = currentReleasePort;
        this.latestReleasePort = latestReleasePort;
        this.downloadPort = downloadPort;
        this.unzipPort = unzipPort;
        this.installPort = installPort;
        this.jarPath = jarPath;
    }

    @Override
    public boolean isUpdateAvailable() {
        final Release latestRelease = latestReleasePort.getRelease();
        final Release currentRelease = currentReleasePort.getRelease();
        if (latestRelease.exists()) {
            return currentRelease.date().isBefore(latestRelease.date());
        } else {
            return false;
        }
    }

    @SneakyThrows
    @Override
    public void runUpdate() {
        final Release latestRelease = latestReleasePort.getRelease();
        if (latestRelease.exists()) {
            final Path fileZip = jarPath.resolve("update.zip");
            downloadPort.downloadFrom(latestRelease.getDownloadUrl(), fileZip);
            unzipPort.unzipTo(jarPath.resolve("update"),  fileZip);
            Files.deleteIfExists(fileZip);

            installPort.installTo(jarPath);
        } else {
            throw new UnsupportedOperationException("No update exists.");
        }
    }
}
