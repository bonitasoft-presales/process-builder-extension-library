package com.bonitasoft.processbuilder.enums;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link RestContentType} enum.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("RestContentType Property-Based Tests")
class RestContentTypePropertyTest {

    // =========================================================================
    // ENUM INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("All enum constants should have non-null MIME type")
    void allConstantsShouldHaveNonNullMimeType(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(type.getMimeType()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("All enum constants should have non-null description")
    void allConstantsShouldHaveNonNullDescription(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(type.getDescription()).isNotNull().isNotBlank();
    }

    @Property(tries = 100)
    @Label("getKey should return same value as getMimeType")
    void getKeyShouldReturnSameAsMimeType(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(type.getKey()).isEqualTo(type.getMimeType());
    }

    @Property(tries = 100)
    @Label("Enum ordinal() should be valid index")
    void enumOrdinalShouldBeValidIndex(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(type.ordinal())
            .isGreaterThanOrEqualTo(0)
            .isLessThan(RestContentType.values().length);
    }

    // =========================================================================
    // IS VALID PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isValid should return true for all MIME types")
    void isValidShouldReturnTrueForMimeTypes(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(RestContentType.isValid(type.getMimeType())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should be case insensitive")
    void isValidShouldBeCaseInsensitive(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(RestContentType.isValid(type.getMimeType().toUpperCase())).isTrue();
        assertThat(RestContentType.isValid(type.getMimeType().toLowerCase())).isTrue();
    }

    @Property(tries = 100)
    @Label("isValid should handle MIME types with charset")
    void isValidShouldHandleMimeTypesWithCharset(@ForAll @From("contentTypes") RestContentType type) {
        String withCharset = type.getMimeType() + "; charset=utf-8";
        assertThat(RestContentType.isValid(withCharset)).isTrue();
    }

    // =========================================================================
    // FROM MIME TYPE PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("fromMimeType should return present for all MIME types")
    void fromMimeTypeShouldReturnPresentForMimeTypes(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(RestContentType.fromMimeType(type.getMimeType())).isPresent();
        assertThat(RestContentType.fromMimeType(type.getMimeType()).get()).isEqualTo(type);
    }

    @Property(tries = 100)
    @Label("fromMimeType should be case insensitive")
    void fromMimeTypeShouldBeCaseInsensitive(@ForAll @From("contentTypes") RestContentType type) {
        assertThat(RestContentType.fromMimeType(type.getMimeType().toUpperCase())).isPresent();
    }

    @Property(tries = 100)
    @Label("fromMimeType should strip charset parameter")
    void fromMimeTypeShouldStripCharsetParameter(@ForAll @From("contentTypes") RestContentType type) {
        String withCharset = type.getMimeType() + "; charset=ISO-8859-1";
        assertThat(RestContentType.fromMimeType(withCharset)).isPresent();
        assertThat(RestContentType.fromMimeType(withCharset).get()).isEqualTo(type);
    }

    // =========================================================================
    // TEXT-BASED PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isTextBased should be consistent with type")
    void isTextBasedShouldBeConsistent(@ForAll @From("contentTypes") RestContentType type) {
        boolean isText = type.isTextBased();
        if (type == RestContentType.JSON || type == RestContentType.XML ||
            type == RestContentType.TEXT_PLAIN || type == RestContentType.TEXT_HTML) {
            assertThat(isText).isTrue();
        } else {
            assertThat(isText).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isJson should only return true for JSON")
    void isJsonShouldOnlyReturnTrueForJson(@ForAll @From("contentTypes") RestContentType type) {
        if (type == RestContentType.JSON) {
            assertThat(type.isJson()).isTrue();
        } else {
            assertThat(type.isJson()).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("isXml should only return true for XML")
    void isXmlShouldOnlyReturnTrueForXml(@ForAll @From("contentTypes") RestContentType type) {
        if (type == RestContentType.XML) {
            assertThat(type.isXml()).isTrue();
        } else {
            assertThat(type.isXml()).isFalse();
        }
    }

    // =========================================================================
    // COLLECTION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getAllData should return map with all constants")
    void getAllDataShouldReturnMapWithAllConstants() {
        Map<String, String> data = RestContentType.getAllData();
        assertThat(data).hasSize(RestContentType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllKeysList should return list with all MIME types")
    void getAllKeysListShouldReturnListWithAllKeys() {
        List<String> keys = RestContentType.getAllKeysList();
        assertThat(keys).hasSize(RestContentType.values().length);
    }

    @Property(tries = 100)
    @Label("getAllData keys should match getAllKeysList")
    void getAllDataKeysShouldMatchGetAllKeysList() {
        Map<String, String> data = RestContentType.getAllData();
        List<String> keys = RestContentType.getAllKeysList();
        assertThat(data.keySet()).containsExactlyInAnyOrderElementsOf(keys);
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<RestContentType> contentTypes() {
        return Arbitraries.of(RestContentType.values());
    }
}
