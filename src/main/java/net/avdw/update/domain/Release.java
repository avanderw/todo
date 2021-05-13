package net.avdw.update.domain;

import java.net.URL;
import java.time.LocalDateTime;

public interface Release {

    LocalDateTime date();

    URL getDownloadUrl();

    boolean exists();
}
