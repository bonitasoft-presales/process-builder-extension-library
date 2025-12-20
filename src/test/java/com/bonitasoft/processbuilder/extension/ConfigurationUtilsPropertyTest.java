package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link ConfigurationUtils} utility class.
 * Tests the masking functionality and configuration lookup behavior.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ConfigurationUtils Property-Based Tests")
class ConfigurationUtilsPropertyTest {

    // =========================================================================
    // MASK IF SENSITIVE PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("maskIfSensitive should return masked value when sensitive is true and value is not empty")
    void maskIfSensitiveShouldReturnMaskedWhenSensitive(
            @ForAll @StringLength(min = 1, max = 200) String value) {

        String result = ConfigurationUtils.maskIfSensitive(value, true);

        assertThat(result).isEqualTo(ConfigurationUtils.MASKED_VALUE);
    }

    @Property(tries = 500)
    @Label("maskIfSensitive should return original value when sensitive is false")
    void maskIfSensitiveShouldReturnOriginalWhenNotSensitive(
            @ForAll @StringLength(min = 0, max = 200) String value) {

        String result = ConfigurationUtils.maskIfSensitive(value, false);

        assertThat(result).isEqualTo(value);
    }

    @Property(tries = 100)
    @Label("maskIfSensitive should return null when value is null (regardless of sensitivity)")
    void maskIfSensitiveShouldReturnNullWhenValueIsNull(
            @ForAll boolean isSensitive) {

        String result = ConfigurationUtils.maskIfSensitive(null, isSensitive);

        assertThat(result).isNull();
    }

    @Property(tries = 100)
    @Label("maskIfSensitive should return empty string when value is empty (regardless of sensitivity)")
    void maskIfSensitiveShouldReturnEmptyWhenValueIsEmpty(
            @ForAll boolean isSensitive) {

        String result = ConfigurationUtils.maskIfSensitive("", isSensitive);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("maskIfSensitive should never throw exception")
    void maskIfSensitiveShouldNeverThrow(
            @ForAll String value,
            @ForAll boolean isSensitive) {

        assertThatCode(() -> ConfigurationUtils.maskIfSensitive(value, isSensitive))
                .doesNotThrowAnyException();
    }

    // =========================================================================
    // MASKED VALUE CONSTANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("MASKED_VALUE constant should be non-null and non-empty")
    void maskedValueConstantShouldBeValid() {
        assertThat(ConfigurationUtils.MASKED_VALUE)
                .isNotNull()
                .isNotEmpty();
    }

    // =========================================================================
    // LOOKUP CONFIGURATION VALUE PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when fullNameKey is null")
    void lookupShouldReturnDefaultWhenFullNameKeyIsNull(
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        Supplier<String> supplier = () -> "someValue";

        String result = ConfigurationUtils.lookupConfigurationValue(
                null, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when fullNameKey is blank")
    void lookupShouldReturnDefaultWhenFullNameKeyIsBlank(
            @ForAll @IntRange(min = 1, max = 20) int spaces,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        String blankKey = " ".repeat(spaces);
        Supplier<String> supplier = () -> "someValue";

        String result = ConfigurationUtils.lookupConfigurationValue(
                blankKey, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when supplier is null")
    void lookupShouldReturnDefaultWhenSupplierIsNull(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", null, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when supplier returns null")
    void lookupShouldReturnDefaultWhenSupplierReturnsNull(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        Supplier<String> supplier = () -> null;

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when supplier returns empty string")
    void lookupShouldReturnDefaultWhenSupplierReturnsEmpty(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        Supplier<String> supplier = () -> "";

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return default when supplier returns blank string")
    void lookupShouldReturnDefaultWhenSupplierReturnsBlank(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll @IntRange(min = 1, max = 20) int spaces,
            @ForAll boolean isSensitive) {

        String blankValue = " ".repeat(spaces);
        Supplier<String> supplier = () -> blankValue;

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(defaultValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should return trimmed supplier value when valid")
    void lookupShouldReturnTrimmedValueWhenValid(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String configValue,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        String paddedValue = "  " + configValue + "  ";
        Supplier<String> supplier = () -> paddedValue;

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(configValue);
    }

    @Property(tries = 300)
    @Label("lookupConfigurationValue should throw RuntimeException when supplier throws")
    void lookupShouldThrowWhenSupplierThrows(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll @StringLength(min = 1, max = 100) String errorMessage,
            @ForAll boolean isSensitive) {

        Supplier<String> supplier = () -> {
            throw new RuntimeException(errorMessage);
        };

        assertThatThrownBy(() -> ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, "ENTITY", supplier, defaultValue, isSensitive))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining(fullNameKey);
    }

    // =========================================================================
    // OVERLOADED METHOD PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("Overloaded lookupConfigurationValue should work without entityTypeKey")
    void overloadedLookupShouldWork(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String fullNameKey,
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String configValue,
            @ForAll @StringLength(min = 1, max = 50) String defaultValue,
            @ForAll boolean isSensitive) {

        Supplier<String> supplier = () -> configValue;

        String result = ConfigurationUtils.lookupConfigurationValue(
                fullNameKey, supplier, defaultValue, isSensitive);

        assertThat(result).isEqualTo(configValue);
    }
}
