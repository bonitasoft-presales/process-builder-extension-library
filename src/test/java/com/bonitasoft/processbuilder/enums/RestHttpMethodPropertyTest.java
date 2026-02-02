package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestHttpMethod} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestHttpMethod Property-Based Tests")
class RestHttpMethodPropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null key")
    void allConstantsShouldHaveNonNullKey(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(method.getKey()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(method.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("Enum name() should never throw")
    void enumNameShouldNeverThrow(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThatCode(() -> method.name()).doesNotThrowAnyException();
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(method.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(RestHttpMethod.values().length);
    }

    // =========================================================================
    // IS VALID PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for all enum names")
    void isValidShouldReturnTrueForEnumNames(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(RestHttpMethod.isValid(method.name())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should return true for all enum keys")
    void isValidShouldReturnTrueForEnumKeys(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(RestHttpMethod.isValid(method.getKey())).isTrue();
    }

    @Property(tries = 200)
    @Label("isValid should be case insensitive")
    void isValidShouldBeCaseInsensitive(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(RestHttpMethod.isValid(method.name().toLowerCase())).isTrue();
        assertThat(RestHttpMethod.isValid(method.name().toUpperCase())).isTrue();
    }

    // =========================================================================
    // FROM KEY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromKey should return present for all enum keys")
    void fromKeyShouldReturnPresentForEnumKeys(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(RestHttpMethod.fromKey(method.getKey())).isPresent();
        assertThat(RestHttpMethod.fromKey(method.getKey()).get()).isEqualTo(method);
    }

    @Property(tries = 100)
    @Label("fromKey should be case insensitive")
    void fromKeyShouldBeCaseInsensitive(@ForAll @From("httpMethods") RestHttpMethod method) {
        assertThat(RestHttpMethod.fromKey(method.getKey().toLowerCase())).isPresent();
    }

    // =========================================================================
    // SUPPORTS BODY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("supportsBody should be consistent with HTTP standards")
    void supportsBodyShouldBeConsistent(@ForAll @From("httpMethods") RestHttpMethod method) {
        boolean supports = method.supportsBody();
        if (method == RestHttpMethod.POST || method == RestHttpMethod.PUT || method == RestHttpMethod.PATCH) {
            assertThat(supports).isTrue();
        } else {
            assertThat(supports).isFalse();
        }
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = RestHttpMethod.getAllData();
        assertThat(data).hasSize(RestHttpMethod.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all keys")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = RestHttpMethod.getAllKeysList();
        assertThat(keys).hasSize(RestHttpMethod.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = RestHttpMethod.getAllData();
        List<String> keys = RestHttpMethod.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestHttpMethod> httpMethods() {
        return Arbitraries.of(RestHttpMethod.values());
    }
}
