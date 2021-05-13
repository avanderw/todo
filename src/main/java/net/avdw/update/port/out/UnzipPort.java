package net.avdw.update.port.out;

import java.nio.file.Path;

public interface UnzipPort {
    void unzipTo(Path destDir, Path fileZip);
}
