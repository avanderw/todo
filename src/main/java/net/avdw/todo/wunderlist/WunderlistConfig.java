package net.avdw.todo.wunderlist;

import com.github.scribejava.apis.WunderlistAPI;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import org.pmw.tinylog.Logger;

class WunderlistConfig extends AbstractModule {
    private String apiKey;
    private String apiSecret;
    private String clientKey;

    void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    @Override
    protected void configure() {
        System.setProperty("java.net.useSystemProxies", "true");
        bind(IgnoreSsl.class).asEagerSingleton();
        bind(String.class).annotatedWith(Names.named("clientKey")).toInstance(clientKey);
    }

    @Provides
    @Singleton
    OAuth20Service service() {
        Logger.debug("Create client");
        return new ServiceBuilder(apiKey)
                .apiSecret(apiSecret)
                .build(WunderlistAPI.instance());
    }
}
