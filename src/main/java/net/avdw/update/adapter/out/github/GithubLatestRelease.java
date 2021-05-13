package net.avdw.update.adapter.out.github;

import net.avdw.todo.SuppressFBWarnings;
import net.avdw.update.domain.Release;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SuppressFBWarnings("UWF_UNWRITTEN_PUBLIC_OR_PROTECTED_FIELD")
class GithubLatestRelease implements Release {
    String created_at;
    GithubReleaseAsset[] assets;

    @Override
    public LocalDateTime date() {
        Instant instant = Instant.parse(created_at);
        return LocalDateTime.ofInstant(instant, ZoneOffset.systemDefault());
    }

    @Override
    public URL getDownloadUrl() {
        URL url = null;
        for (GithubReleaseAsset asset : assets) {
            if (url == null) {
                url = asset.browser_download_url;
            }
        }
        if (url == null) {
            throw new UnsupportedOperationException("No download url found.");
        }
        return url;
    }

    @Override
    public boolean exists() {
        return created_at != null && assets.length != 0;
    }
}
