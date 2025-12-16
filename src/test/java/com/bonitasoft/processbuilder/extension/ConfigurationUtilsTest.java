package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigurationUtils}.
 * <p>
 * Tests cover all public methods including edge cases, boundary conditions,
 * and error handling to achieve 100% code coverage.
 * </p>
 */
class ConfigurationUtilsTest {

    private static final String TEST_KEY = "TestKey";
    private static final String TEST_ENTITY_TYPE = "TEST_ENTITY";
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String CONFIG_VALUE = "configValue";

    // -------------------------------------------------------------------------
    // Constructor Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should throw UnsupportedOperationException")
        void constructor_should_prevent_instantiation() throws Exception {
            Constructor<ConfigurationUtils> constructor = ConfigurationUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            InvocationTargetException exception = assertThrows(
                InvocationTargetException.class,
                constructor::newInstance
            );

            assertThat(exception.getCause())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("cannot be instantiated");
        }
    }

    // -------------------------------------------------------------------------
    // Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Constants Tests")
    class ConstantsTests {

        @Test
        @DisplayName("MASKED_VALUE should be defined correctly")
        void masked_value_should_be_defined() {
            assertEquals("********", ConfigurationUtils.MASKED_VALUE);
        }
    }

    // -------------------------------------------------------------------------
    // lookupConfigurationValue Tests (5-parameter version)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("lookupConfigurationValue (5 params) Tests")
    class LookupConfigurationValueFullTests {

        @Test
        @DisplayName("Should return config value when found and not sensitive")
        void should_return_config_value_when_found() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> CONFIG_VALUE,
                DEFAULT_VALUE,
                false
            );

            assertEquals(CONFIG_VALUE, result);
        }

        @Test
        @DisplayName("Should return trimmed config value")
        void should_return_trimmed_config_value() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> "  trimmedValue  ",
                DEFAULT_VALUE,
                false
            );

            assertEquals("trimmedValue", result);
        }

        @Test
        @DisplayName("Should return config value when sensitive (value still returned, just masked in logs)")
        void should_return_config_value_when_sensitive() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> "secretPassword",
                DEFAULT_VALUE,
                true
            );

            assertEquals("secretPassword", result);
        }

        @Test
        @DisplayName("Should return default value when supplier returns null")
        void should_return_default_when_supplier_returns_null() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> null,
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when supplier returns empty string")
        void should_return_default_when_supplier_returns_empty() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> "",
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when supplier returns blank string")
        void should_return_default_when_supplier_returns_blank() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                () -> "   ",
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when fullNameKey is null")
        void should_return_default_when_fullNameKey_is_null() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                null,
                TEST_ENTITY_TYPE,
                () -> CONFIG_VALUE,
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when fullNameKey is blank")
        void should_return_default_when_fullNameKey_is_blank() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                "   ",
                TEST_ENTITY_TYPE,
                () -> CONFIG_VALUE,
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when supplier is null")
        void should_return_default_when_supplier_is_null() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                null,
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should throw RuntimeException when supplier throws exception")
        void should_throw_exception_when_supplier_throws() {
            Supplier<String> failingSupplier = () -> {
                throw new RuntimeException("DAO connection failed");
            };

            assertThatThrownBy(() -> ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                TEST_ENTITY_TYPE,
                failingSupplier,
                DEFAULT_VALUE,
                false
            ))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to retrieve")
            .hasMessageContaining(TEST_KEY)
            .hasCauseInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("Should work with null entityTypeKey")
        void should_work_with_null_entityTypeKey() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                null,
                () -> CONFIG_VALUE,
                DEFAULT_VALUE,
                false
            );

            assertEquals(CONFIG_VALUE, result);
        }
    }

    // -------------------------------------------------------------------------
    // lookupConfigurationValue Tests (4-parameter version)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("lookupConfigurationValue (4 params) Tests")
    class LookupConfigurationValueShortTests {

        @Test
        @DisplayName("Should return config value using 4-param overload")
        void should_return_config_value_with_4_params() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                () -> CONFIG_VALUE,
                DEFAULT_VALUE,
                false
            );

            assertEquals(CONFIG_VALUE, result);
        }

        @Test
        @DisplayName("Should return default value when supplier returns null (4-param)")
        void should_return_default_when_null_4_params() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                () -> null,
                DEFAULT_VALUE,
                false
            );

            assertEquals(DEFAULT_VALUE, result);
        }

        @Test
        @DisplayName("Should mask sensitive value in 4-param overload")
        void should_handle_sensitive_value_4_params() {
            String result = ConfigurationUtils.lookupConfigurationValue(
                TEST_KEY,
                () -> "secret",
                DEFAULT_VALUE,
                true
            );

            assertEquals("secret", result);
        }
    }

    // -------------------------------------------------------------------------
    // maskIfSensitive Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("maskIfSensitive Tests")
    class MaskIfSensitiveTests {

        @Test
        @DisplayName("Should return masked value when sensitive is true")
        void should_mask_when_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive("password123", true);
            assertEquals(ConfigurationUtils.MASKED_VALUE, result);
        }

        @Test
        @DisplayName("Should return original value when sensitive is false")
        void should_not_mask_when_not_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive("normalValue", false);
            assertEquals("normalValue", result);
        }

        @Test
        @DisplayName("Should return null when value is null and sensitive")
        void should_return_null_when_null_and_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive(null, true);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return null when value is null and not sensitive")
        void should_return_null_when_null_and_not_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive(null, false);
            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty string when value is empty and sensitive")
        void should_return_empty_when_empty_and_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive("", true);
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should return empty string when value is empty and not sensitive")
        void should_return_empty_when_empty_and_not_sensitive() {
            String result = ConfigurationUtils.maskIfSensitive("", false);
            assertEquals("", result);
        }
    }
}
