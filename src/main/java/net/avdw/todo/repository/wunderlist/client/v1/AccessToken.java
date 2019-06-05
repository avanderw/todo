package net.avdw.todo.repository.wunderlist.client.v1;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AccessToken {
    private final OAuth20Service service;
    private final String clientKey;
    private OAuth2AccessToken accessToken;

    @Inject
    AccessToken(OAuth20Service service, @Named("clientKey") String clientKey) {
        this.service = service;
        this.clientKey = clientKey;
    }

    public OAuth2AccessToken get() {
        if (this.accessToken == null) {
            try {
                this.accessToken = this.service.getAccessToken(this.clientKey);
            } catch (IOException | InterruptedException | ExecutionException e) {
                Logger.error(e);
                throw new UnsupportedOperationException();
            }
        }

        return this.accessToken;
    }
}
