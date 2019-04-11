package net.avdw.todo.playground;

import com.github.scribejava.apis.WunderlistAPI;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import net.avdw.todo.wunderlist.IgnoreSsl;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class WunderlistListExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, KeyManagementException, NoSuchAlgorithmException {
        new IgnoreSsl();
        final String apiKey = "34be69e3313a17355d82";
        final String apiSecret = "869630c64d9bf7b065e48d8a059978a64e6ab12b4e13800764f3fa9b4c7e";
        final String code = "bcf5a937726fc583e183";

        final OAuth20Service service = new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .build(WunderlistAPI.instance());
        final OAuth2AccessToken accessToken = service.getAccessToken(code);

        final OAuthRequest request = new OAuthRequest(Verb.GET, "https://a.wunderlist.com/api/v1/lists");
        request.addQuerystringParameter("title", "Groceries");
        service.signRequest(accessToken, request);
        final Response response = service.execute(request);
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        System.out.println(response.getCode());
        System.out.println(response.getBody());
    }
}
