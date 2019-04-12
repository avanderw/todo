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

    private Integer listId;
    private Integer revision;
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
        try {
            final OAuthRequest request = new OAuthRequest(Verb.GET, "https://a.wunderlist.com/api/v1/lists");
            service.signRequest(accessToken, request);
            final Response response = service.execute(request);
            Type listType = new TypeToken<List<ListModel>>() {
            }.getType();
            Logger.debug(response.getBody());
            List<ListModel> lists = new Gson().fromJson(response.getBody(), listType);
            Logger.debug(lists);

            boolean listExists = lists.stream().filter(l -> l.title.equals(listName)).count() == 1;
            if (listExists) {
                lists.stream().filter(l -> l.title.equals(listName)).findAny().ifPresent(l -> {
                    listId = l.id;
                    revision = l.revision;
                });
            }

            return listExists;
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.error(e);
        }

        return false;
    }

    public void deleteDatabase() {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.DELETE, String.format("https://a.wunderlist.com/api/v1/lists/%s", listId));
            request.addHeader("Content-Type", "application/json");
            request.addQuerystringParameter("revision", revision.toString());
            service.signRequest(accessToken, request);
            Logger.debug(request.toString());
            final Response response = service.execute(request);
            Logger.debug(response.getBody());
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.error(e);
        }
    }

    public void createDatabase() {
        try {
            if (!databaseExists()) {
                final OAuthRequest request = new OAuthRequest(Verb.POST, "https://a.wunderlist.com/api/v1/lists");
                request.addHeader("Content-Type", "application/json");
                Logger.debug("listName={}", listName);
                request.setPayload(String.format("{\"title\":\"%s\"}", listName));
                Logger.debug(request.getStringPayload());
                service.signRequest(accessToken, request);
                final Response response = service.execute(request);
                Logger.debug(response.getBody());
                ListModel model = new Gson().fromJson(response.getBody(), ListModel.class);
                Logger.debug(model);
                listId = model.id;
            }
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.error(e);
        }
    }

    public void addTask(String todo) {
        try {
            final OAuthRequest request = new OAuthRequest(Verb.POST, "https://a.wunderlist.com/api/v1/tasks");
            request.addHeader("Content-Type", "application/json");
            request.setPayload(String.format("{\"list_id\":%s, \"title\":\"%s\"}", listId, todo));
            service.signRequest(accessToken, request);
            Logger.debug(request.toString());
            Logger.debug(request.getStringPayload());
            final Response response = service.execute(request);
            Logger.debug(response.getBody());
        } catch (InterruptedException | ExecutionException | IOException e) {
            Logger.error(e);
        }
    }
}
