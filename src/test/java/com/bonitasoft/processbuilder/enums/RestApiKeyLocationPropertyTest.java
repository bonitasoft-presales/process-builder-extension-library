package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestApiKeyLocation} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestApiKeyLocation Property-Based Tests")
class RestApiKeyLocationPropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(location.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(location.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(location.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(RestApiKeyLocation.values().length);
    }

    // =========================================================================
    // IS VALID PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for all enum names")
    void isValidShouldReturnTrueForEnumNames(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(RestApiKeyLocation.isValid(location.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for all enum keys")
    void isValidShouldReturnTrueForEnumKeys(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(RestApiKeyLocation.isValid(location.getKey())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should be case insensitive for enum names")
    void isValidShouldBeCaseInsensitive(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(RestApiKeyLocation.isValid(location.name().toLowerCase())).isTrue();
        assertThat(RestApiKeyLocation.isValid(location.name().toUpperCase())).isTrue();
    }

    // =========================================================================
    // FROM KEY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return present for all enum keys")
    void fromKeyShouldReturnPresentForEnumKeys(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(RestApiKeyLocation.fromKey(location.getKey())).isPresent();
        assertThat(RestApiKeyLocation.fromKey(location.getKey()).get()).isEqualTo(location);
    }

    @Property(tries = 100)
    @Label("fromKey should be case insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("locations") RestApiKeyLocation location) {
        assertThat(RestApiKeyLocation.fromKey(location.getKey().toUpperCase())).isPresent();
        assertThat(RestApiKeyLocation.fromKey(location.getKey().toLowerCase())).isPresent();
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = RestApiKeyLocation.getAllData();
        assertThat(data).hasSize(RestApiKeyLocation.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all keys")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = RestApiKeyLocation.getAllKeysList();
        assertThat(keys).hasSize(RestApiKeyLocation.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = RestApiKeyLocation.getAllData();
        List<String> keys = RestApiKeyLocation.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestApiKeyLocation> locations() {
        return Arbitraries.of(RestApiKeyLocation.values());
    }
}
