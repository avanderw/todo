package net.avdw.todo.wunderlist;

import com.google.inject.Inject;
import org.pmw.tinylog.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class IgnoreSsl {
    @Inject
    public IgnoreSsl() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    Logger.debug("ignoring");
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                    Logger.debug("ignoring");
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                    Logger.debug("ignoring");
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    sslContext.getSocketFactory());

            HttpsURLConnection
                    .setDefaultHostnameVerifier((arg0, arg1) -> {
                        Logger.debug("ignoring");
                        return true;
                    });

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            Logger.error(e);
        }
    }
}
