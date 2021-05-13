package net.avdw.update.adapter.out.jar;

import net.avdw.update.domain.Release;

import java.net.URL;
import java.time.LocalDateTime;

class JarCurrentRelease implements Release {
    @Override
    public LocalDateTime date() {
        return LocalDateTime.parse("2020-05-01T12:54");
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
