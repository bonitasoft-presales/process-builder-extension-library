package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class for {@link GenericType} enum.
 * Ensures 100% code coverage including all enum constants, methods, and edge cases.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("GenericType Enum Tests")
class GenericTypeTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final int EXPECTED_ENUM_COUNT = 2;

    // =========================================================================
    // ENUM CONSTANTS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Enum Constants Tests")
    class EnumConstantsTests {

        @Test
        @DisplayName("values() should return all 2 enum constants")
        void values_should_return_all_constants() {
            GenericType[] values = GenericType.values();

            assertThat(values)
                .hasSize(EXPECTED_ENUM_COUNT)
                .containsExactly(
                    GenericType.LANG,
                    GenericType.HOST
                );
        }

        @Test
        @DisplayName("LANG should have correct key and description")
        void lang_should_have_correct_attributes() {
            GenericType lang = GenericType.LANG;

            assertThat(lang.getKey()).isEqualTo("lang");
            assertThat(lang.getDescription())
                .contains("language")
                .contains("internationalization");
            assertThat(lang.name()).isEqualTo("LANG");
            assertThat(lang.ordinal()).isEqualTo(0);
        }

        @Test
        @DisplayName("HOST should have correct key and description")
        void host_should_have_correct_attributes() {
            GenericType host = GenericType.HOST;

            assertThat(host.getKey()).isEqualTo("host");
            assertThat(host.getDescription())
                .contains("URL")
                .contains("host");
            assertThat(host.name()).isEqualTo("HOST");
            assertThat(host.ordinal()).isEqualTo(1);
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
            assertThat(GenericType.isValid("LANG")).isTrue();
            assertThat(GenericType.isValid("HOST")).isTrue();
        }

        @Test
        @DisplayName("should return true for valid lowercase names")
        void should_return_true_for_valid_lowercase_names() {
            assertThat(GenericType.isValid("lang")).isTrue();
            assertThat(GenericType.isValid("host")).isTrue();
        }

        @Test
        @DisplayName("should return true for mixed case names")
        void should_return_true_for_mixed_case_names() {
            assertThat(GenericType.isValid("Lang")).isTrue();
            assertThat(GenericType.isValid("HoSt")).isTrue();
        }

        @Test
        @DisplayName("should return true for names with leading/trailing spaces")
        void should_return_true_for_names_with_spaces() {
            assertThat(GenericType.isValid("  LANG  ")).isTrue();
            assertThat(GenericType.isValid("\tHOST\t")).isTrue();
        }

        @Test
        @DisplayName("should return false for null input")
        void should_return_false_for_null() {
            assertThat(GenericType.isValid(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty string")
        void should_return_false_for_empty_string() {
            assertThat(GenericType.isValid("")).isFalse();
        }

        @Test
        @DisplayName("should return false for whitespace only")
        void should_return_false_for_whitespace_only() {
            assertThat(GenericType.isValid("   ")).isFalse();
            assertThat(GenericType.isValid("\t")).isFalse();
            assertThat(GenericType.isValid("\n")).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid names")
        void should_return_false_for_invalid_names() {
            assertThat(GenericType.isValid("INVALID")).isFalse();
            assertThat(GenericType.isValid("LANGUAGE")).isFalse();
            assertThat(GenericType.isValid("URL")).isFalse();
            assertThat(GenericType.isValid("config")).isFalse();
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
            Map<String, String> data = GenericType.getAllData();

            assertThat(data).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain correct keys")
        void should_contain_correct_keys() {
            Map<String, String> data = GenericType.getAllData();

            assertThat(data.keySet())
                .containsExactlyInAnyOrder("lang", "host");
        }

        @Test
        @DisplayName("should map keys to correct descriptions")
        void should_map_keys_to_correct_descriptions() {
            Map<String, String> data = GenericType.getAllData();

            assertThat(data.get("lang")).contains("language");
            assertThat(data.get("host")).contains("URL");
        }

        @Test
        @DisplayName("should return unmodifiable map")
        void should_return_unmodifiable_map() {
            Map<String, String> data = GenericType.getAllData();

            assertThatThrownBy(() -> data.put("NEW_KEY", "New Value"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from map")
        void should_not_allow_removal_from_map() {
            Map<String, String> data = GenericType.getAllData();

            assertThatThrownBy(() -> data.remove("lang"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on map")
        void should_not_allow_clear_on_map() {
            Map<String, String> data = GenericType.getAllData();

            assertThatThrownBy(() -> data.clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should map each key to its enum description")
        void should_map_each_key_to_enum_description() {
            Map<String, String> data = GenericType.getAllData();

            for (GenericType type : GenericType.values()) {
                assertThat(data.get(type.getKey())).isEqualTo(type.getDescription());
            }
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
            List<String> keys = GenericType.getAllKeysList();

            assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain all expected keys")
        void should_contain_all_expected_keys() {
            List<String> keys = GenericType.getAllKeysList();

            assertThat(keys)
                .containsExactlyInAnyOrder("lang", "host");
        }

        @Test
        @DisplayName("should preserve enum declaration order")
        void should_preserve_enum_declaration_order() {
            List<String> keys = GenericType.getAllKeysList();

            assertThat(keys).containsExactly("lang", "host");
        }

        @Test
        @DisplayName("should return unmodifiable list")
        void should_return_unmodifiable_list() {
            List<String> keys = GenericType.getAllKeysList();

            assertThatThrownBy(() -> keys.add("NEW_KEY"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from list")
        void should_not_allow_removal_from_list() {
            List<String> keys = GenericType.getAllKeysList();

            assertThatThrownBy(() -> keys.remove(0))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on list")
        void should_not_allow_clear_on_list() {
            List<String> keys = GenericType.getAllKeysList();

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
            assertThat(GenericType.valueOf("LANG")).isEqualTo(GenericType.LANG);
            assertThat(GenericType.valueOf("HOST")).isEqualTo(GenericType.HOST);
        }

        @Test
        @DisplayName("should throw exception for invalid name")
        void should_throw_exception_for_invalid_name() {
            assertThatThrownBy(() -> GenericType.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw exception for null name")
        void should_throw_exception_for_null_name() {
            assertThatThrownBy(() -> GenericType.valueOf(null))
                .isInstanceOf(NullPointerException.class);
        }
    }
}
