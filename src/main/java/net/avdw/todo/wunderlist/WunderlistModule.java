package net.avdw.todo.wunderlist;

import com.github.scribejava.apis.WunderlistAPI;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class WunderlistModule extends AbstractModule {
    @Override
    protected void configure() {
        File propertiesFile = new File("wunderlist.properties");
        WunderlistProperties wunderlistProperties = new WunderlistProperties(propertiesFile);
        Names.bindProperties(binder(), wunderlistProperties.load());
        bind(String.class).annotatedWith(Names.named("API_KEY")).toInstance("34be69e3313a17355d82");
        bind(String.class).annotatedWith(Names.named("API_SECRET")).toInstance("869630c64d9bf7b065e48d8a059978a64e6ab12b4e13800764f3fa9b4c7e");
        bind(String.class).annotatedWith(Names.named("CLIENT_AUTH")).toInstance("bcf5a937726fc583e183");
        bind(WunderlistClientOld.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    OAuth20Service getService(@Named("API_KEY") String apiKey, @Named("API_SECRET") String apiSecret) {
        return new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .build(WunderlistAPI.instance());
    }

    @Provides
    @Singleton
    OAuth2AccessToken getAccessToken(OAuth20Service service, @Named("CLIENT_AUTH") String code) {
        try {
            return service.getAccessToken(code);
        } catch (IOException | InterruptedException | ExecutionException e) {
            Logger.error(e);
        }
        return new OAuth2AccessToken("token_failure");
    }
}
