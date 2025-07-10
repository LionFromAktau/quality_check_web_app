package com.qualityinspection.swequalityinspection.config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PreDestroy;


@Configuration
public class KeycloakConfig {

    private static final String SERVER_URL = "http://keycloak:8080";
    private static final String REALM = "master";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String CLIENT_ID = "admin-cli";

    public static final String OAUTH2REALM = "oauth2-realm";

    private Keycloak keycloak;

    @Bean
    public Keycloak keycloakAdminClient() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM)
                .username(ADMIN_USERNAME)
                .password(ADMIN_PASSWORD)
                .clientId(CLIENT_ID)
                .build();
        return keycloak;
    }

    @PreDestroy
    public void closeKeycloakClient() {
        if (keycloak != null) {
            keycloak.close();
            System.out.println("Keycloak client closed on application shutdown.");
        }
    }
}

