package com.gmail.detection.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GmailConfig {

    private static final String APPLICATION_NAME = "AI Gmail Management System";

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES =
            Collections.singletonList(GmailScopes.GMAIL_MODIFY);

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    // IMPORTANT: this bean performs an interactive OAuth handshake (it can open
    // a browser window and block waiting for a Google login) the first time it
    // runs. @Lazy means Spring won't touch it during application startup - it's
    // only created the first time some Gmail feature is actually used, and a
    // cached token in tokens/ lets subsequent runs skip the browser step
    // entirely. Without @Lazy, the whole application (including tests) would
    // refuse to start until that login flow was completed by hand.
    @Bean
    @Lazy
    public Gmail gmailService() throws Exception {

        System.out.println("\n==========================================");
        System.out.println("      Gmail Configuration Started");
        System.out.println("==========================================");

        Credential credential = authorize();

        System.out.println("Authorization Successful.");
        System.out.println("Creating Gmail Service...");

        Gmail gmail = new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();

        System.out.println("Gmail API Connected Successfully.");
        System.out.println("==========================================\n");

        return gmail;
    }

    private Credential authorize()
            throws IOException, GeneralSecurityException {

        System.out.println("Loading credentials.json...");

        InputStream inputStream =
                GmailConfig.class.getResourceAsStream("/credentials.json");

        if (inputStream == null) {
            throw new RuntimeException(
                    "credentials.json NOT FOUND inside src/main/resources");
        }

        System.out.println("credentials.json loaded successfully.");

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY,
                        new InputStreamReader(inputStream)
                );

        System.out.println("Building OAuth Flow...");

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        GoogleNetHttpTransport.newTrustedTransport(),
                        JSON_FACTORY,
                        clientSecrets,
                        SCOPES
                )
                        .setDataStoreFactory(
                                new FileDataStoreFactory(
                                        new File(TOKENS_DIRECTORY_PATH)
                                )
                        )
                        .setAccessType("offline")
                        .build();

        System.out.println("OAuth Flow Created.");

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder()
                        .setPort(8888)
                        .build();

        System.out.println("------------------------------------------");
        System.out.println("Opening Google Login Page...");
        System.out.println("If browser doesn't open:");
        System.out.println("Copy the URL from console into browser.");
        System.out.println("------------------------------------------");

        Credential credential =
                new AuthorizationCodeInstalledApp(flow, receiver)
                        .authorize("user");

        System.out.println("Google Login Completed.");
        System.out.println("Access Token Saved Successfully.");

        return credential;
    }
}