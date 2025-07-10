package com.qualityinspection.swequalityinspection.config;

import com.qualityinspection.swequalityinspection.model.enums.AppRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/home").permitAll()
                        .requestMatchers("/api/user/me").authenticated()

                        .requestMatchers("/").hasAnyAuthority(
                                AppRole.admin.asAuthority(),
                                AppRole.productionWorker.asAuthority(),
                                AppRole.qualityManager.asAuthority(),
                                AppRole.qualityInspector.asAuthority(),
                                AppRole.productionManager.asAuthority()
                        )

                        .requestMatchers("/api/defects/**").hasAnyAuthority(
                                AppRole.admin.asAuthority(),
                                AppRole.qualityManager.asAuthority(),
                                AppRole.qualityInspector.asAuthority()
                        )

                        .requestMatchers("/api/checklists/**").hasAnyAuthority(
                                AppRole.admin.asAuthority(),
                                AppRole.productionWorker.asAuthority(),
                                AppRole.qualityInspector.asAuthority()
                        )

                        .requestMatchers("/api/products/**", "/api/batches/**").hasAnyAuthority(
                                AppRole.admin.asAuthority(),
                                AppRole.qualityManager.asAuthority(),
                                AppRole.qualityInspector.asAuthority(),
                                AppRole.productionManager.asAuthority(),
                                AppRole.productionWorker.asAuthority()
                        )

                        .requestMatchers("/api/reports/**").hasAnyAuthority(
                                AppRole.admin.asAuthority(),
                                AppRole.qualityManager.asAuthority(),
                                AppRole.productionManager.asAuthority()
                        )

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/api/batches/authenticated", true)
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(keycloakLogoutSuccessHandler(clientRegistrationRepository, authorizedClientRepository))
                        .logoutSuccessUrl("http://keycloak:8080/realms/oauth2-realm/protocol/openid-connect/logout?redirect_uri=http://localhost:8080/home")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public LogoutSuccessHandler keycloakLogoutSuccessHandler(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        return (request, response, authentication) -> {
            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                var clientRegistration = clientRegistrationRepository.findByRegistrationId("oauth2-realm-client");

                String endSessionEndpoint = clientRegistration.getProviderDetails()
                        .getConfigurationMetadata().get("end_session_endpoint").toString();

                String idToken = oidcUser.getIdToken().getTokenValue();

                String logoutUrl = UriComponentsBuilder
                        .fromUriString(endSessionEndpoint)
                        .queryParam("id_token_hint", idToken)
                        .queryParam("post_logout_redirect_uri", "http://localhost:8080/home")
                        .build()
                        .toUriString();

                response.sendRedirect(logoutUrl);
            } else {
                response.sendRedirect("/home");
            }
        };
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                    .map(claims -> (List<String>) claims.get("roles"))
                    .orElse(Collections.emptyList());

            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        });
        return converter;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            for (var authority : authorities) {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    var userInfo = oidcUserAuthority.getUserInfo();
                    if (userInfo.hasClaim("realm_access")) {
                        var realmAccess = (Map<String, Object>) userInfo.getClaim("realm_access");
                        var roles = (Collection<String>) realmAccess.get("roles");

                        for (String role : roles) {
                            for (AppRole appRole : AppRole.values()) {
                                if (role.equalsIgnoreCase(appRole.asAuthority())) {
                                    mappedAuthorities.add(new SimpleGrantedAuthority(appRole.asAuthority()));
                                }
                            }
                        }
                    }
                }
            }
            return mappedAuthorities;
        };
    }
}
