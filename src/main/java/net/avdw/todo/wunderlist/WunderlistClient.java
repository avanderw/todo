package net.avdw.todo.wunderlist;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WunderlistClient {

    private final String listName;
    private final OAuth20Service service;
    private final OAuth2AccessToken accessToken;

    @Inject
    public WunderlistClient(@Named("WUNDERLIST_NAME") String listName, OAuth20Service service, OAuth2AccessToken accessToken) {
        this.listName = listName;
        this.service = service;
        this.accessToken = accessToken;
    }

    public boolean databaseExists() {
        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://a.wunderlist.com/api/v1/lists");
        try {
            service.signRequest(accessToken, request);
            final Response response = service.execute(request);
            Type listType = new TypeToken<List<ListModel>>(){}.getType();
            Logger.debug(response.getBody());
            List<ListModel> lists = new Gson().fromJson(response.getBody(), listType);
            Logger.debug(lists);
            return lists.stream().filter(l->l.title.equals(listName)).count() == 1;
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.error(e);
        }

        return false;
    }

    public void deleteDatabase() {
        throw new UnsupportedOperationException();
    }

    public void createDatabase() {
        if (!databaseExists()) {

        }
    }
}
