package com.bonitasoft.processbuilder.records;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link LoadedSchema} record.
 * Tests invariants that must hold for any valid instance,
 * especially focusing on immutability and defensive copying.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("LoadedSchema Property-Based Tests")
class LoadedSchemaPropertyTest {

    // =========================================================================
    // IMMUTABILITY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Titles map should be immutable (cannot be modified externally after construction)")
    void titlesMapShouldBeImmutable(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String targetSchemaName,
            @ForAll @StringLength(min = 1, max = 200) String jsonInput,
            @ForAll Map<@StringLength(min = 1, max = 30) @AlphaChars String,
                       @StringLength(min = 1, max = 50) @AlphaChars String> titles) {

        Map<String, String> mutableTitles = new HashMap<>(titles);
        LoadedSchema schema = new LoadedSchema(null, mutableTitles, targetSchemaName, jsonInput);

        // Attempt to modify the original map after construction
        mutableTitles.put("SHOULD_NOT_APPEAR", "value");

        // The record's internal map should not be affected
        assertThat(schema.titles()).doesNotContainKey("SHOULD_NOT_APPEAR");
    }

    @Property(tries = 300)
    @Label("Titles accessor should return unmodifiable map")
    void titlesAccessorShouldReturnUnmodifiableMap(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String targetSchemaName,
            @ForAll @StringLength(min = 1, max = 200) String jsonInput,
            @ForAll Map<@StringLength(min = 1, max = 30) @AlphaChars String,
                       @StringLength(min = 1, max = 50) @AlphaChars String> titles) {

        LoadedSchema schema = new LoadedSchema(null, titles, targetSchemaName, jsonInput);

        assertThatThrownBy(() -> schema.titles().put("NEW_KEY", "NEW_VALUE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // EQUALITY PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Equality should be reflexive")
    void equalityShouldBeReflexive(@ForAll @From("validLoadedSchemas") LoadedSchema schema) {
        assertThat(schema).isEqualTo(schema);
    }

    @Property(tries = 300)
    @Label("Equality should be symmetric")
    void equalityShouldBeSymmetric(
            @ForAll @From("validLoadedSchemas") LoadedSchema schema1,
            @ForAll @From("validLoadedSchemas") LoadedSchema schema2) {
        assertThat(schema1.equals(schema2)).isEqualTo(schema2.equals(schema1));
    }

    @Property(tries = 300)
    @Label("Equal objects should have equal hashCodes")
    void equalObjectsShouldHaveEqualHashCodes(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String targetSchemaName,
            @ForAll @StringLength(min = 1, max = 200) String jsonInput,
            @ForAll Map<@StringLength(min = 1, max = 30) @AlphaChars String,
                       @StringLength(min = 1, max = 50) @AlphaChars String> titles) {

        LoadedSchema schema1 = new LoadedSchema(null, titles, targetSchemaName, jsonInput);
        LoadedSchema schema2 = new LoadedSchema(null, titles, targetSchemaName, jsonInput);

        assertThat(schema1).isEqualTo(schema2);
        assertThat(schema1.hashCode()).isEqualTo(schema2.hashCode());
    }

    @Property(tries = 300)
    @Label("equals(null) should return false")
    void equalsNullShouldReturnFalse(@ForAll @From("validLoadedSchemas") LoadedSchema schema) {
        assertThat(schema.equals(null)).isFalse();
    }

    // =========================================================================
    // HASHCODE PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("HashCode should be stable across multiple calls")
    void hashCodeShouldBeStable(@ForAll @From("validLoadedSchemas") LoadedSchema schema) {
        int hash1 = schema.hashCode();
        int hash2 = schema.hashCode();
        int hash3 = schema.hashCode();

        assertThat(hash1).isEqualTo(hash2).isEqualTo(hash3);
    }

    // =========================================================================
    // TOSTRING PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("toString should never return null")
    void toStringShouldNeverReturnNull(@ForAll @From("validLoadedSchemas") LoadedSchema schema) {
        assertThat(schema.toString()).isNotNull();
    }

    @Property(tries = 300)
    @Label("toString should contain class name")
    void toStringShouldContainClassName(@ForAll @From("validLoadedSchemas") LoadedSchema schema) {
        assertThat(schema.toString()).contains("LoadedSchema");
    }

    // =========================================================================
    // ACCESSOR PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Accessors should return constructor values")
    void accessorsShouldReturnConstructorValues(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String targetSchemaName,
            @ForAll @StringLength(min = 1, max = 200) String jsonInput,
            @ForAll Map<@StringLength(min = 1, max = 30) @AlphaChars String,
                       @StringLength(min = 1, max = 50) @AlphaChars String> titles) {

        LoadedSchema schema = new LoadedSchema(null, titles, targetSchemaName, jsonInput);

        assertThat(schema.validator()).isNull();
        assertThat(schema.targetSchemaName()).isEqualTo(targetSchemaName);
        assertThat(schema.jsonInput()).isEqualTo(jsonInput);
        assertThat(schema.titles()).containsAllEntriesOf(titles);
    }

    @Property(tries = 300)
    @Label("Titles map size should be preserved")
    void titlesMapSizeShouldBePreserved(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String targetSchemaName,
            @ForAll @StringLength(min = 1, max = 200) String jsonInput,
            @ForAll Map<@StringLength(min = 1, max = 30) @AlphaChars String,
                       @StringLength(min = 1, max = 50) @AlphaChars String> titles) {

        LoadedSchema schema = new LoadedSchema(null, titles, targetSchemaName, jsonInput);

        assertThat(schema.titles()).hasSize(titles.size());
    }

    // =========================================================================
    // ARBITRARIES (DATA GENERATORS)
    // =========================================================================

    @Provide
    Arbitrary<LoadedSchema> validLoadedSchemas() {
        Arbitrary<String> schemaNames = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50);
        Arbitrary<String> jsonInputs = Arbitraries.strings().ofMinLength(1).ofMaxLength(200);
        Arbitrary<Map<String, String>> titleMaps = Arbitraries.maps(
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30),
                Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(50)
        ).ofMinSize(0).ofMaxSize(10);

        return Combinators.combine(schemaNames, jsonInputs, titleMaps)
                .as((name, json, titles) -> new LoadedSchema(null, titles, name, json));
    }
}
