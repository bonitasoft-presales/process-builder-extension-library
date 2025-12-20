package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class for {@link ConfigurationType} enum.
 * Ensures 100% code coverage including all enum constants, methods, and edge cases.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("ConfigurationType Enum Tests")
class ConfigurationTypeTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final int EXPECTED_ENUM_COUNT = 3;

    // =========================================================================
    // ENUM CONSTANTS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Enum Constants Tests")
    class EnumConstantsTests {

        @Test
        @DisplayName("values() should return all 3 enum constants")
        void values_should_return_all_constants() {
            ConfigurationType[] values = ConfigurationType.values();

            assertThat(values)
                .hasSize(EXPECTED_ENUM_COUNT)
                .containsExactly(
                    ConfigurationType.SMTP,
                    ConfigurationType.PROC_EXECUTION_CONNECTOR,
                    ConfigurationType.THEME
                );
        }

        @Test
        @DisplayName("SMTP should have correct key and description")
        void smtp_should_have_correct_attributes() {
            ConfigurationType smtp = ConfigurationType.SMTP;

            assertThat(smtp.getKey()).isEqualTo("Smtp");
            assertThat(smtp.getDescription())
                .contains("SMTP server configuration")
                .contains("email notifications");
            assertThat(smtp.name()).isEqualTo("SMTP");
        }

        @Test
        @DisplayName("PROC_EXECUTION_CONNECTOR should have correct key and description")
        void proc_execution_connector_should_have_correct_attributes() {
            ConfigurationType connector = ConfigurationType.PROC_EXECUTION_CONNECTOR;

            assertThat(connector.getKey()).isEqualTo("ProcExecutionConnector");
            assertThat(connector.getDescription())
                .contains("process execution connector")
                .contains("workflow integrations");
            assertThat(connector.name()).isEqualTo("PROC_EXECUTION_CONNECTOR");
        }

        @Test
        @DisplayName("THEME should have correct key and description")
        void theme_should_have_correct_attributes() {
            ConfigurationType theme = ConfigurationType.THEME;

            assertThat(theme.getKey()).isEqualTo("Theme");
            assertThat(theme.getDescription())
                .contains("visual theme configuration")
                .contains("branding elements");
            assertThat(theme.name()).isEqualTo("THEME");
        }
    }

    // =========================================================================
    // isValid() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("isValid() Method Tests")
    class IsValidMethodTests {

        @Test
        @DisplayName("should return true for valid uppercase names")
        void should_return_true_for_valid_uppercase_names() {
            assertThat(ConfigurationType.isValid("SMTP")).isTrue();
            assertThat(ConfigurationType.isValid("PROC_EXECUTION_CONNECTOR")).isTrue();
            assertThat(ConfigurationType.isValid("THEME")).isTrue();
        }

        @Test
        @DisplayName("should return true for valid lowercase names")
        void should_return_true_for_valid_lowercase_names() {
            assertThat(ConfigurationType.isValid("smtp")).isTrue();
            assertThat(ConfigurationType.isValid("proc_execution_connector")).isTrue();
            assertThat(ConfigurationType.isValid("theme")).isTrue();
        }

        @Test
        @DisplayName("should return true for mixed case names")
        void should_return_true_for_mixed_case_names() {
            assertThat(ConfigurationType.isValid("Smtp")).isTrue();
            assertThat(ConfigurationType.isValid("Proc_Execution_Connector")).isTrue();
            assertThat(ConfigurationType.isValid("ThEmE")).isTrue();
        }

        @Test
        @DisplayName("should return true for names with leading/trailing spaces")
        void should_return_true_for_names_with_spaces() {
            assertThat(ConfigurationType.isValid("  SMTP  ")).isTrue();
            assertThat(ConfigurationType.isValid("\tTHEME\t")).isTrue();
            assertThat(ConfigurationType.isValid("  proc_execution_connector  ")).isTrue();
        }

        @Test
        @DisplayName("should return false for null input")
        void should_return_false_for_null() {
            assertThat(ConfigurationType.isValid(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty string")
        void should_return_false_for_empty_string() {
            assertThat(ConfigurationType.isValid("")).isFalse();
        }

        @Test
        @DisplayName("should return false for whitespace only")
        void should_return_false_for_whitespace_only() {
            assertThat(ConfigurationType.isValid("   ")).isFalse();
            assertThat(ConfigurationType.isValid("\t")).isFalse();
            assertThat(ConfigurationType.isValid("\n")).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid names")
        void should_return_false_for_invalid_names() {
            assertThat(ConfigurationType.isValid("INVALID")).isFalse();
            assertThat(ConfigurationType.isValid("EMAIL")).isFalse();
            assertThat(ConfigurationType.isValid("CONFIGURATION")).isFalse();
            assertThat(ConfigurationType.isValid("smtp_config")).isFalse();
        }
    }

    // =========================================================================
    // getAllData() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("getAllData() Method Tests")
    class GetAllDataMethodTests {

        @Test
        @DisplayName("should return map with all enum entries")
        void should_return_map_with_all_entries() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThat(data).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain correct keys")
        void should_contain_correct_keys() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThat(data.keySet())
                .containsExactlyInAnyOrder("Smtp", "ProcExecutionConnector", "Theme");
        }

        @Test
        @DisplayName("should map keys to correct descriptions")
        void should_map_keys_to_correct_descriptions() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThat(data.get("Smtp")).contains("SMTP server configuration");
            assertThat(data.get("ProcExecutionConnector")).contains("process execution connector");
            assertThat(data.get("Theme")).contains("visual theme configuration");
        }

        @Test
        @DisplayName("should return unmodifiable map")
        void should_return_unmodifiable_map() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThatThrownBy(() -> data.put("NEW_KEY", "New Value"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from map")
        void should_not_allow_removal_from_map() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThatThrownBy(() -> data.remove("Smtp"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on map")
        void should_not_allow_clear_on_map() {
            Map<String, String> data = ConfigurationType.getAllData();

            assertThatThrownBy(() -> data.clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================================
    // getAllKeysList() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("getAllKeysList() Method Tests")
    class GetAllKeysListMethodTests {

        @Test
        @DisplayName("should return list with all keys")
        void should_return_list_with_all_keys() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain all expected keys")
        void should_contain_all_expected_keys() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThat(keys)
                .containsExactlyInAnyOrder("Smtp", "ProcExecutionConnector", "Theme");
        }

        @Test
        @DisplayName("should preserve enum declaration order")
        void should_preserve_enum_declaration_order() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThat(keys).containsExactly("Smtp", "ProcExecutionConnector", "Theme");
        }

        @Test
        @DisplayName("should return unmodifiable list")
        void should_return_unmodifiable_list() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThatThrownBy(() -> keys.add("NEW_KEY"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from list")
        void should_not_allow_removal_from_list() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThatThrownBy(() -> keys.remove(0))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on list")
        void should_not_allow_clear_on_list() {
            List<String> keys = ConfigurationType.getAllKeysList();

            assertThatThrownBy(() -> keys.clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // =========================================================================
    // valueOf() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("valueOf() Method Tests")
    class ValueOfMethodTests {

        @Test
        @DisplayName("should return correct enum for valid name")
        void should_return_correct_enum_for_valid_name() {
            assertThat(ConfigurationType.valueOf("SMTP")).isEqualTo(ConfigurationType.SMTP);
            assertThat(ConfigurationType.valueOf("PROC_EXECUTION_CONNECTOR"))
                .isEqualTo(ConfigurationType.PROC_EXECUTION_CONNECTOR);
            assertThat(ConfigurationType.valueOf("THEME")).isEqualTo(ConfigurationType.THEME);
        }

        @Test
        @DisplayName("should throw exception for invalid name")
        void should_throw_exception_for_invalid_name() {
            assertThatThrownBy(() -> ConfigurationType.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw exception for null name")
        void should_throw_exception_for_null_name() {
            assertThatThrownBy(() -> ConfigurationType.valueOf(null))
                .isInstanceOf(NullPointerException.class);
        }
    }
}
