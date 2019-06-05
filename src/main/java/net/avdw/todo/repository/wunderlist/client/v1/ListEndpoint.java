package net.avdw.todo.repository.wunderlist.client.v1;

import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ListEndpoint {
    private final Gson gson;
    private final RestClient restClient;

    @Inject
    ListEndpoint(RestClient restClient) {
        this.restClient = restClient;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<AList> getLists() {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://a.wunderlist.com/api/v1/lists");
        final Response response = this.restClient.execute(request);
        if (response.getCode() == 200) {
            try {
                String body = response.getBody();
                return Arrays.asList(this.gson.fromJson(body, AList[].class));
            } catch (IOException e) {
                Logger.error(e);
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
