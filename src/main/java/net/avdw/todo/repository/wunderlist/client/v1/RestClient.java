package net.avdw.todo.repository.wunderlist.client.v1;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

class RestClient {
    private final OAuth20Service service;
    private final AccessToken accessToken;

    @Inject
    RestClient(OAuth20Service service, AccessToken accessToken) {
        this.service = service;
        this.accessToken = accessToken;
    }

    Response execute(OAuthRequest request) {
        service.signRequest(accessToken.get(), request);
        try {
            return service.execute(request);
        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new UnsupportedOperationException();
        }
    }
}
