package net.avdw.todo.playground;
// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// [START tasks_quickstart]

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.TasksScopes;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import javax.net.ssl.*;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

public class GoogleTaskQuickStart {
    private static final String APPLICATION_NAME = "Google Tasks API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(TasksScopes.TASKS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleTaskQuickStart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.proxyHost", "proxyuser.int.capinet");
        System.setProperty("https.proxyHost", "proxyuser.int.capinet");
        System.setProperty("http.proxyPort", "3128");
        System.setProperty("https.proxyPort", "3128");
        System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
        System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
        System.setProperty("http.proxyUser", "s_bamboo");
        System.setProperty("https.proxyUser", "s_bamboo");
        System.setProperty("http.proxyPassword", "xqLS9fU0X8ebGmv");
        System.setProperty("https.proxyPassword", "xqLS9fU0X8ebGmv");

        // Java ignores http.proxyUser. Here come's the workaround.
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (getRequestorType() == RequestorType.PROXY) {
                    String prot = getRequestingProtocol().toLowerCase();
                    String host = System.getProperty(prot + ".proxyHost", "");
                    String port = System.getProperty(prot + ".proxyPort", "80");
                    String user = System.getProperty(prot + ".proxyUser", "");
                    String password = System.getProperty(prot + ".proxyPassword", "");
                    if (getRequestingHost().equalsIgnoreCase(host)) {
                        if (Integer.parseInt(port) == getRequestingPort()) {
                            return new PasswordAuthentication(user, password.toCharArray());
                        }
                    }
                }
                return null;
            }
        });

        StringBuilder content = new StringBuilder();
        URL url = new URL("https://www.google.com");
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line).append("\n");
        }
        bufferedReader.close();
        if (content.toString().isEmpty()) {
            System.out.println("No connection made");
        } else {
            System.out.println("Connection made");
        }

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Tasks service = new Tasks.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // Print the first 10 task lists.
        TaskLists result = service.tasklists().list()
                .setMaxResults(10L)
                .execute();
        List<TaskList> taskLists = result.getItems();
        if (taskLists == null || taskLists.isEmpty()) {
            System.out.println("No task lists found.");
        } else {
            System.out.println("Task lists:");
            for (TaskList tasklist : taskLists) {
                System.out.printf("%s (%s)\n", tasklist.getTitle(), tasklist.getId());
            }
        }
    }
}
// [END tasks_quickstart]
