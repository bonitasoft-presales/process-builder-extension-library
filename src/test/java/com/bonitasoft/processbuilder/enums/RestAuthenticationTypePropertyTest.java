package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestAuthenticationType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestAuthenticationType Property-Based Tests")
class RestAuthenticationTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(type.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum name() should never throw")
    void enumNameShouldNeverThrow(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThatCode(() -> type.name()).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(RestAuthenticationType.values().length);
    }

    // =========================================================================
    // IS VALID PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for all enum names")
    void isValidShouldReturnTrueForEnumNames(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(RestAuthenticationType.isValid(type.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for all enum keys")
    void isValidShouldReturnTrueForEnumKeys(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(RestAuthenticationType.isValid(type.getKey())).isTrue();
    }

    @Property(tries = 200)
    @Label("isValid should be case insensitive for enum names")
    void isValidShouldBeCaseInsensitiveForNames(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(RestAuthenticationType.isValid(type.name().toLowerCase())).isTrue();
        assertThat(RestAuthenticationType.isValid(type.name().toUpperCase())).isTrue();
    }

    @Property(tries = 500)
    @Label("isValid should return false for random strings")
    void isValidShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String input) {
        // Skip if input matches any valid key or name
        boolean matchesKey = RestAuthenticationType.fromKey(input).isPresent();
        boolean matchesName = false;
        try {
            RestAuthenticationType.valueOf(input.toUpperCase());
            matchesName = true;
        } catch (IllegalArgumentException ignored) {}

        if (!matchesKey && !matchesName) {
            assertThat(RestAuthenticationType.isValid(input)).isFalse();
        }
    }

    // =========================================================================
    // FROM KEY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return present for all enum keys")
    void fromKeyShouldReturnPresentForEnumKeys(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(RestAuthenticationType.fromKey(type.getKey())).isPresent();
        assertThat(RestAuthenticationType.fromKey(type.getKey()).get()).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("fromKey should be case insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("authTypes") RestAuthenticationType type) {
        assertThat(RestAuthenticationType.fromKey(type.getKey().toUpperCase())).isPresent();
        assertThat(RestAuthenticationType.fromKey(type.getKey().toLowerCase())).isPresent();
    }

    // =========================================================================
    // BEHAVIORAL PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("requiresCredentials should be consistent with type")
    void requiresCredentialsShouldBeConsistent(@ForAll @From("authTypes") RestAuthenticationType type) {
        boolean requires = type.requiresCredentials();
        if (type == RestAuthenticationType.BASIC || type == RestAuthenticationType.DIGEST ||
            type == RestAuthenticationType.NTLM || type == RestAuthenticationType.OAUTH2_PASSWORD) {
            assertThat(requires).isTrue();
        } else {
            assertThat(requires).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("requiresOAuth2TokenExchange should be consistent with type")
    void requiresOAuth2TokenExchangeShouldBeConsistent(@ForAll @From("authTypes") RestAuthenticationType type) {
        boolean requires = type.requiresOAuth2TokenExchange();
        if (type == RestAuthenticationType.OAUTH2_CLIENT_CREDENTIALS ||
            type == RestAuthenticationType.OAUTH2_PASSWORD) {
            assertThat(requires).isTrue();
        } else {
            assertThat(requires).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("usesStaticToken should be consistent with type")
    void usesStaticTokenShouldBeConsistent(@ForAll @From("authTypes") RestAuthenticationType type) {
        boolean uses = type.usesStaticToken();
        if (type == RestAuthenticationType.BEARER || type == RestAuthenticationType.API_KEY) {
            assertThat(uses).isTrue();
        } else {
            assertThat(uses).isFalse();
        }
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = RestAuthenticationType.getAllData();
        assertThat(data).hasSize(RestAuthenticationType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all keys")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = RestAuthenticationType.getAllKeysList();
        assertThat(keys).hasSize(RestAuthenticationType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = RestAuthenticationType.getAllData();
        List<String> keys = RestAuthenticationType.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestAuthenticationType> authTypes() {
        return Arbitraries.of(RestAuthenticationType.values());
    }
}
