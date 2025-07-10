package com.qualityinspection.swequalityinspection.config;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.qualityinspection.swequalityinspection.model.enums.AppRole;

@Component
@Order(1)
public class KeycloakUserSeeder implements ApplicationRunner {

    private static final String REALM = KeycloakConfig.OAUTH2REALM;

    private static final Map<String, String> roleToUsername = Map.of(
            AppRole.admin.asAuthority(), "admin",
            AppRole.qualityManager.asAuthority(), "qualitymanager1",
            AppRole.qualityInspector.asAuthority(), "qualityinspector1",
            AppRole.productionWorker.asAuthority(), "productionworker1",
            AppRole.productionManager.asAuthority(), "productionmanager1"
    );

    private final Keycloak keycloak;

    public KeycloakUserSeeder(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Map.Entry<String, String> entry : roleToUsername.entrySet()) {
            String roleName = entry.getKey();
            String username = entry.getValue();

            List<UserRepresentation> existing = keycloak.realm(REALM).users().search(username);
            if (!existing.isEmpty()) {
                System.out.println("⚠️ User " + username + " already exists. Skipping.");
                continue;
            }

            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEnabled(true);
            Response response = keycloak.realm(REALM).users().create(user);
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            response.close();

            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue("admin");
            cred.setTemporary(false);
            keycloak.realm(REALM).users().get(userId).resetPassword(cred);

            RoleRepresentation role = keycloak.realm(REALM).roles().get(roleName).toRepresentation();
            keycloak.realm(REALM).users().get(userId).roles().realmLevel().add(List.of(role));

            System.out.println("✅ Created user '" + username + "' with role '" + roleName + "'");
        }
    }

}
