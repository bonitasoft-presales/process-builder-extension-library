package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestOAuth2ClientAuthMethod} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestOAuth2ClientAuthMethod Property-Based Tests")
class RestOAuth2ClientAuthMethodPropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(method.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(method.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(method.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(RestOAuth2ClientAuthMethod.values().length);
    }

    // =========================================================================
    // IS VALID PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for all enum names")
    void isValidShouldReturnTrueForEnumNames(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(RestOAuth2ClientAuthMethod.isValid(method.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for all enum keys")
    void isValidShouldReturnTrueForEnumKeys(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(RestOAuth2ClientAuthMethod.isValid(method.getKey())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should be case insensitive for enum names")
    void isValidShouldBeCaseInsensitive(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(RestOAuth2ClientAuthMethod.isValid(method.name().toLowerCase())).isTrue();
        assertThat(RestOAuth2ClientAuthMethod.isValid(method.name().toUpperCase())).isTrue();
    }

    // =========================================================================
    // FROM KEY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return present for all enum keys")
    void fromKeyShouldReturnPresentForEnumKeys(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(RestOAuth2ClientAuthMethod.fromKey(method.getKey())).isPresent();
        assertThat(RestOAuth2ClientAuthMethod.fromKey(method.getKey()).get()).isEqualTo(method);
    }

    @Property(tries = 100)
    @Label("fromKey should be case insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("methods") RestOAuth2ClientAuthMethod method) {
        assertThat(RestOAuth2ClientAuthMethod.fromKey(method.getKey().toUpperCase())).isPresent();
        assertThat(RestOAuth2ClientAuthMethod.fromKey(method.getKey().toLowerCase())).isPresent();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = RestOAuth2ClientAuthMethod.getAllData();
        assertThat(data).hasSize(RestOAuth2ClientAuthMethod.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all keys")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = RestOAuth2ClientAuthMethod.getAllKeysList();
        assertThat(keys).hasSize(RestOAuth2ClientAuthMethod.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = RestOAuth2ClientAuthMethod.getAllData();
        List<String> keys = RestOAuth2ClientAuthMethod.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestOAuth2ClientAuthMethod> methods() {
        return Arbitraries.of(RestOAuth2ClientAuthMethod.values());
    }
}
