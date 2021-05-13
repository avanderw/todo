package net.avdw.update.adapter.out.github;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import net.avdw.update.UpdateFeature;
import net.avdw.update.domain.Release;
import net.avdw.update.port.out.LatestReleasePort;
import org.tinylog.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Singleton
class GithubLatestReleaseAdapter implements LatestReleasePort {
    private final URI uri;
    private Release latestRelease;

    @Inject
    GithubLatestReleaseAdapter(@UpdateFeature final URI uri) {
        this.uri = uri;
    }

    @SneakyThrows
    @Override
    public Release getRelease() {
        if (latestRelease == null) {
            Logger.debug("Request: '{}'", uri);
            HttpRequest request = HttpRequest.newBuilder(uri).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String content = response.body();
            Logger.debug("   Body: \n{}", content);
            latestRelease = switch (response.statusCode()) {
                case 200 -> new Gson().fromJson(content, GithubLatestRelease.class);
                default -> new GithubLatestRelease();
            };
        }
        return latestRelease;
    }
}

