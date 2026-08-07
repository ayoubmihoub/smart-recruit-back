package com.irrigo.userservice.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.admin-realm}")
    private String adminRealm;

    @Value("${keycloak.admin-client-id}")
    private String clientId;

    @Value("${keycloak.admin-username}")
    private String username;

    @Value("${keycloak.admin-password}")
    private String password;

    public String createUser(
            String email,
            String firstName,
            String lastName,
            String userPassword,
            String role
    ) {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();

        RealmResource realmResource = keycloak.realm(realm);

        UserRepresentation user = new UserRepresentation();

        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userPassword);
        credential.setTemporary(false);

        user.setCredentials(List.of(credential));

        Response response = realmResource
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException(
                    "Failed to create Keycloak user : " + response.getStatus()
            );
        }

        String location = response.getHeaderString("Location");

        String keycloakId =
                location.substring(location.lastIndexOf("/") + 1);

        RoleRepresentation roleRepresentation =
                realmResource
                        .roles()
                        .get(role)
                        .toRepresentation();

        realmResource
                .users()
                .get(keycloakId)
                .roles()
                .realmLevel()
                .add(List.of(roleRepresentation));

        response.close();
        keycloak.close();

        return keycloakId;
    }

}