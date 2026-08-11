package com.irrigo.userservice.service;

import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
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

    @Value("${keycloak.client-id}")
    private String userClientId;

    @Value("${keycloak.client-secret:}")
    private String userClientSecret;

    private Keycloak getKeycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }

    public String createUser(
            String email,
            String firstName,
            String lastName,
            String userPassword,
            String role
    ) {

        log.info(
                "Starting Keycloak user creation for email: {}",
                email
        );

        Keycloak keycloak = null;
        Response response = null;

        try {

            keycloak = getKeycloak();

            RealmResource realmResource =
                    keycloak.realm(realm);

            UserRepresentation user =
                    new UserRepresentation();

            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(true);
            user.setRequiredActions(
                    Collections.emptyList()
            );

            CredentialRepresentation credential =
                    new CredentialRepresentation();

            credential.setType(
                    CredentialRepresentation.PASSWORD
            );

            credential.setValue(userPassword);
            credential.setTemporary(false);

            user.setCredentials(
                    List.of(credential)
            );

            response = realmResource
                    .users()
                    .create(user);

            int status = response.getStatus();

            if (status == 409) {

                throw new RuntimeException(
                        "User already exists in Keycloak"
                );
            }

            if (status != 201) {

                String error =
                        response.readEntity(String.class);

                log.error(
                        "Keycloak user creation failed: {}",
                        error
                );

                throw new RuntimeException(
                        "Failed to create Keycloak user : "
                                + status
                );
            }

            String location =
                    response.getHeaderString("Location");

            String keycloakId =
                    location.substring(
                            location.lastIndexOf("/") + 1
                    );

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
                    .add(
                            List.of(roleRepresentation)
                    );

            return keycloakId;

        } catch (Exception e) {

            log.error(
                    "Keycloak creation error for email: {}",
                    email,
                    e
            );

            throw e;

        } finally {

            if (response != null) {
                response.close();
            }

            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public void updateUser(
            String keycloakId,
            String email,
            String firstName,
            String lastName
    ) {

        Keycloak keycloak = null;

        try {

            keycloak = getKeycloak();

            RealmResource realmResource =
                    keycloak.realm(realm);

            UserResource userResource =
                    realmResource
                            .users()
                            .get(keycloakId);

            UserRepresentation user =
                    userResource.toRepresentation();

            if (email != null &&
                    !email.isBlank()) {

                user.setEmail(email);
            }

            if (firstName != null &&
                    !firstName.isBlank()) {

                user.setFirstName(firstName);
            }

            if (lastName != null &&
                    !lastName.isBlank()) {

                user.setLastName(lastName);
            }

            userResource.update(user);

        } finally {

            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public UserRepresentation getUser(
            String keycloakId
    ) {

        Keycloak keycloak = null;

        try {

            keycloak = getKeycloak();

            return keycloak
                    .realm(realm)
                    .users()
                    .get(keycloakId)
                    .toRepresentation();

        } finally {

            if (keycloak != null) {
                keycloak.close();
            }
        }
    }

    public void updatePassword(
            String keycloakId,
            String currentPassword,
            String newPassword
    ) {

        RestTemplate restTemplate =
                new RestTemplate();

        String url =
                serverUrl
                        + "/realms/"
                        + realm
                        + "/protocol/openid-connect/token";

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.APPLICATION_FORM_URLENCODED
        );

        MultiValueMap<String, String> body =
                new LinkedMultiValueMap<>();

        body.add(
                "grant_type",
                "password"
        );

        body.add(
                "client_id",
                userClientId
        );

        body.add(
                "username",
                getUser(keycloakId).getUsername()
        );

        body.add(
                "password",
                currentPassword
        );

        if (userClientSecret != null &&
                !userClientSecret.isBlank()) {

            body.add(
                    "client_secret",
                    userClientSecret
            );
        }

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(
                        body,
                        headers
                );

        try {

            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

        } catch (HttpClientErrorException.Unauthorized e) {

            throw new RuntimeException(
                    "Current password is incorrect"
            );
        }

        resetPassword(
                keycloakId,
                newPassword
        );
    }

    public void resetPassword(
            String keycloakId,
            String newPassword
    ) {

        Keycloak keycloak = null;

        try {

            keycloak = getKeycloak();

            UserResource userResource =
                    keycloak
                            .realm(realm)
                            .users()
                            .get(keycloakId);

            CredentialRepresentation credential =
                    new CredentialRepresentation();

            credential.setType(
                    CredentialRepresentation.PASSWORD
            );

            credential.setValue(newPassword);
            credential.setTemporary(false);

            userResource.resetPassword(
                    credential
            );

        } finally {

            if (keycloak != null) {
                keycloak.close();
            }
        }
    }
}