```java
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GmailConfig {

    private static final String APPLICATION_NAME =
            "AI Gmail Management System";

    private static final GsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES =
            Collections.singletonList(GmailScopes.GMAIL_MODIFY);

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Creates Gmail API service lazily.
     *
     * @Lazy prevents Gmail OAuth from running while the application
     * is starting. The Gmail service is created only when it is needed.
     */
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

    /**
     * Creates Google OAuth credentials.
     *
     * The application first looks for GOOGLE_CREDENTIALS_JSON
     * environment variable. This is the recommended approach
     * for Render/production.
     *
     * If the environment variable does not exist, it falls back
     * to credentials.json inside src/main/resources for local development.
     */
    private Credential authorize()
            throws IOException, GeneralSecurityException {

        System.out.println("Loading Google OAuth credentials...");

        InputStream inputStream = null;

        /*
         * ============================================================
         * OPTION 1: Render / Production
         * ============================================================
         *
         * Render should contain:
         *
         * GOOGLE_CREDENTIALS_JSON=<complete credentials.json content>
         */
        String credentialsJson =
                System.getenv("GOOGLE_CREDENTIALS_JSON");

        if (credentialsJson != null && !credentialsJson.isBlank()) {

            System.out.println(
                    "Loading credentials from GOOGLE_CREDENTIALS_JSON..."
            );

            inputStream = new ByteArrayInputStream(
                    credentialsJson.getBytes(StandardCharsets.UTF_8)
            );

            System.out.println(
                    "Google credentials loaded from environment variable."
            );
        }

        /*
         * ============================================================
         * OPTION 2: Local Development
         * ============================================================
         *
         * If GOOGLE_CREDENTIALS_JSON isn't available, look for:
         *
         * src/main/resources/credentials.json
         */
        if (inputStream == null) {

            System.out.println(
                    "GOOGLE_CREDENTIALS_JSON not found."
            );

            System.out.println(
                    "Trying local credentials.json..."
            );

            inputStream =
                    GmailConfig.class.getResourceAsStream(
                            "/credentials.json"
                    );

            if (inputStream == null) {

                throw new RuntimeException(
                        "Google OAuth credentials not found. " +
                        "Set GOOGLE_CREDENTIALS_JSON in Render " +
                        "or place credentials.json inside " +
                        "src/main/resources for local development."
                );
            }

            System.out.println(
                    "Local credentials.json loaded successfully."
            );
        }

        /*
         * Read Google OAuth client configuration.
         */
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY,
                        new InputStreamReader(
                                inputStream,
                                StandardCharsets.UTF_8
                        )
                );

        System.out.println(
                "Google Client Secrets loaded successfully."
        );

        /*
         * ============================================================
         * Create OAuth Flow
         * ============================================================
         */
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

        /*
         * ============================================================
         * Local OAuth Login
         * ============================================================
         *
         * This part is suitable for LOCAL development.
         *
         * Render cannot open a browser automatically, so this
         * interactive flow should not be relied upon for production.
         */
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
                new AuthorizationCodeInstalledApp(
                        flow,
                        receiver
                ).authorize("user");

        System.out.println("Google Login Completed.");
        System.out.println("Access Token Saved Successfully.");

        return credential;
    }
}
```
