package com.qualityinspection.swequalityinspection.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//Returns Access and Response Token for Frontend, (jwt.io to check access_token)
@RestController
@RequestMapping("/api")
public class TokenController {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @GetMapping("/tokens")
    public Map<String, String> getTokens(@AuthenticationPrincipal OidcUser oidcUser) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                "oauth2-realm-client", oidcUser.getName());

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", client.getAccessToken().getTokenValue());

        if (client.getRefreshToken() != null) {
            tokens.put("refresh_token", client.getRefreshToken().getTokenValue());
        }

        return tokens;
    }
}