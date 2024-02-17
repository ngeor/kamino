package com.github.ngeor.yak4j;

import static com.github.ngeor.yak4j.Util.loadSwaggerDocument;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Merger}.
 */
class MergerTest {

    @Test
    void test() throws IOException {
        // arrange
        DefinitionPrefixer definitionPrefixer = new DefinitionPrefixer();
        PathPrefixer pathPrefixer = new PathPrefixer();

        SwaggerDocument firstSwaggerDocument = loadSwaggerDocument("/address-book.yml");
        pathPrefixer.prefix(firstSwaggerDocument, "/address-book");
        definitionPrefixer.prefix(firstSwaggerDocument, "AddressBook");

        SwaggerDocument secondSwaggerDocument = loadSwaggerDocument("/auth.yml");
        pathPrefixer.prefix(secondSwaggerDocument, "/auth");
        definitionPrefixer.prefix(secondSwaggerDocument, "Auth");

        // act
        Merger merger = new Merger();
        merger.merge(firstSwaggerDocument, secondSwaggerDocument);

        // assert
        assertThat(firstSwaggerDocument.getFragment("paths").keys())
                .containsExactly(
                        "/address-book/addresses",
                        "/address-book/addresses/{addressId}",
                        "/auth/login",
                        "/auth/token",
                        "/auth/refresh-token",
                        "/auth/sign-up",
                        "/auth/confirm-sign-up",
                        "/auth/availability",
                        "/auth/users",
                        "/auth/users/{username}",
                        "/auth/users/{username}/confirm",
                        "/auth/users/{username}/reset-password",
                        "/auth/clients",
                        "/auth/clients/{id}",
                        "/auth/clients/{id}/roles",
                        "/auth/clients/{clientId}/roles/{roleId}");
        assertThat(firstSwaggerDocument.getFragment("definitions").keys())
                .containsExactly(
                        "AddressBookAddress",
                        "AddressBookContactInfo",
                        "AuthUserCredentials",
                        "AuthCommonUserProperties",
                        "AuthSignUpRequest",
                        "AuthConfirmSignUpRequest",
                        "AuthAvailabilityRequest",
                        "AuthUserStatus",
                        "AuthRole",
                        "AuthClient",
                        "AuthClaim",
                        "AuthConfirmedUser",
                        "AuthUser",
                        "AuthLoginResult",
                        "AuthTokenVerificationResult",
                        "AuthBillingSchedule",
                        "AuthBillingInfo",
                        "AuthAddress",
                        "AuthContactInfo",
                        "AuthClientDetails");
    }
}
