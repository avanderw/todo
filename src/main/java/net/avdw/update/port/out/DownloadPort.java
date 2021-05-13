package net.avdw.update.port.out;

import java.net.URL;
import java.nio.file.Path;

@FunctionalInterface
public interface DownloadPort {
    void downloadFrom(URL url, Path fileZip);
}
