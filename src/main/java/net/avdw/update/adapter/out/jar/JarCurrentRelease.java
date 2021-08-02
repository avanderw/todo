package net.avdw.update.adapter.out.jar;

import lombok.SneakyThrows;
import net.avdw.update.domain.Release;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

class JarCurrentRelease implements Release {
    @SneakyThrows
    @Override
    public LocalDateTime date() {
        Path path = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).toPath();
        FileTime lastModified = Files.getLastModifiedTime(path);
        return LocalDateTime.ofInstant(lastModified.toInstant(), ZoneId.systemDefault());
    }

    @Override
    public URL getDownloadUrl() {
        return null;
    }

    @Override
    public boolean exists() {
        return false;
    }
}
