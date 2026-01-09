package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test class for {@link DataResolverType} enum.
 * Ensures 100% code coverage including all enum constants, methods, and edge cases.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@DisplayName("DataResolverType Enum Tests")
class DataResolverTypeTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final int EXPECTED_ENUM_COUNT = 6;

    // =========================================================================
    // ENUM CONSTANTS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Enum Constants Tests")
    class EnumConstantsTests {

        @Test
        @DisplayName("values() should return all 6 enum constants")
        void values_should_return_all_constants() {
            DataResolverType[] values = DataResolverType.values();

            assertThat(values)
                .hasSize(EXPECTED_ENUM_COUNT)
                .containsExactly(
                    DataResolverType.RECIPIENT_FIRSTNAME,
                    DataResolverType.RECIPIENT_LASTNAME,
                    DataResolverType.RECIPIENT_EMAIL,
                    DataResolverType.TASK_LINK,
                    DataResolverType.STEP_USER_NAME,
                    DataResolverType.STEP_STATUS
                );
        }

        @Test
        @DisplayName("RECIPIENT_FIRSTNAME should have correct key and description")
        void recipient_firstname_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.RECIPIENT_FIRSTNAME;

            assertThat(type.getKey()).isEqualTo("recipient_firstname");
            assertThat(type.getDescription())
                .contains("first name")
                .contains("recipient");
            assertThat(type.name()).isEqualTo("RECIPIENT_FIRSTNAME");
            assertThat(type.ordinal()).isEqualTo(0);
        }

        @Test
        @DisplayName("RECIPIENT_LASTNAME should have correct key and description")
        void recipient_lastname_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.RECIPIENT_LASTNAME;

            assertThat(type.getKey()).isEqualTo("recipient_lastname");
            assertThat(type.getDescription())
                .contains("last name")
                .contains("recipient");
            assertThat(type.name()).isEqualTo("RECIPIENT_LASTNAME");
            assertThat(type.ordinal()).isEqualTo(1);
        }

        @Test
        @DisplayName("RECIPIENT_EMAIL should have correct key and description")
        void recipient_email_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.RECIPIENT_EMAIL;

            assertThat(type.getKey()).isEqualTo("recipient_email");
            assertThat(type.getDescription())
                .contains("email")
                .contains("recipient");
            assertThat(type.name()).isEqualTo("RECIPIENT_EMAIL");
            assertThat(type.ordinal()).isEqualTo(2);
        }

        @Test
        @DisplayName("TASK_LINK should have correct key and description")
        void task_link_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.TASK_LINK;

            assertThat(type.getKey()).isEqualTo("task_link");
            assertThat(type.getDescription())
                .contains("link")
                .contains("task");
            assertThat(type.name()).isEqualTo("TASK_LINK");
            assertThat(type.ordinal()).isEqualTo(3);
        }

        @Test
        @DisplayName("STEP_USER_NAME should have correct key and description")
        void step_user_name_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.STEP_USER_NAME;

            assertThat(type.getKey()).isEqualTo("step_user_name");
            assertThat(type.getDescription())
                .contains("username")
                .contains("step");
            assertThat(type.name()).isEqualTo("STEP_USER_NAME");
            assertThat(type.ordinal()).isEqualTo(4);
        }

        @Test
        @DisplayName("STEP_STATUS should have correct key and description")
        void step_status_should_have_correct_attributes() {
            DataResolverType type = DataResolverType.STEP_STATUS;

            assertThat(type.getKey()).isEqualTo("step_status");
            assertThat(type.getDescription())
                .contains("status")
                .contains("step");
            assertThat(type.name()).isEqualTo("STEP_STATUS");
            assertThat(type.ordinal()).isEqualTo(5);
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
            assertThat(DataResolverType.isValid("RECIPIENT_FIRSTNAME")).isTrue();
            assertThat(DataResolverType.isValid("RECIPIENT_LASTNAME")).isTrue();
            assertThat(DataResolverType.isValid("RECIPIENT_EMAIL")).isTrue();
            assertThat(DataResolverType.isValid("TASK_LINK")).isTrue();
            assertThat(DataResolverType.isValid("STEP_USER_NAME")).isTrue();
            assertThat(DataResolverType.isValid("STEP_STATUS")).isTrue();
        }

        @Test
        @DisplayName("should return true for valid lowercase names")
        void should_return_true_for_valid_lowercase_names() {
            assertThat(DataResolverType.isValid("recipient_firstname")).isTrue();
            assertThat(DataResolverType.isValid("task_link")).isTrue();
            assertThat(DataResolverType.isValid("step_status")).isTrue();
        }

        @Test
        @DisplayName("should return true for mixed case names")
        void should_return_true_for_mixed_case_names() {
            assertThat(DataResolverType.isValid("Recipient_Firstname")).isTrue();
            assertThat(DataResolverType.isValid("Task_Link")).isTrue();
        }

        @Test
        @DisplayName("should return true for names with leading/trailing spaces")
        void should_return_true_for_names_with_spaces() {
            assertThat(DataResolverType.isValid("  RECIPIENT_FIRSTNAME  ")).isTrue();
            assertThat(DataResolverType.isValid("\tTASK_LINK\t")).isTrue();
        }

        @Test
        @DisplayName("should return false for null input")
        void should_return_false_for_null() {
            assertThat(DataResolverType.isValid(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty string")
        void should_return_false_for_empty_string() {
            assertThat(DataResolverType.isValid("")).isFalse();
        }

        @Test
        @DisplayName("should return false for whitespace only")
        void should_return_false_for_whitespace_only() {
            assertThat(DataResolverType.isValid("   ")).isFalse();
            assertThat(DataResolverType.isValid("\t")).isFalse();
            assertThat(DataResolverType.isValid("\n")).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid names")
        void should_return_false_for_invalid_names() {
            assertThat(DataResolverType.isValid("INVALID")).isFalse();
            assertThat(DataResolverType.isValid("FIRSTNAME")).isFalse();
            assertThat(DataResolverType.isValid("EMAIL")).isFalse();
        }
    }

    // =========================================================================
    // isValidKey() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("isValidKey() Method Tests")
    class IsValidKeyMethodTests {

        @Test
        @DisplayName("should return true for valid keys")
        void should_return_true_for_valid_keys() {
            assertThat(DataResolverType.isValidKey("recipient_firstname")).isTrue();
            assertThat(DataResolverType.isValidKey("recipient_lastname")).isTrue();
            assertThat(DataResolverType.isValidKey("recipient_email")).isTrue();
            assertThat(DataResolverType.isValidKey("task_link")).isTrue();
            assertThat(DataResolverType.isValidKey("step_user_name")).isTrue();
            assertThat(DataResolverType.isValidKey("step_status")).isTrue();
        }

        @Test
        @DisplayName("should return true for keys with leading/trailing spaces")
        void should_return_true_for_keys_with_spaces() {
            assertThat(DataResolverType.isValidKey("  recipient_firstname  ")).isTrue();
            assertThat(DataResolverType.isValidKey("\ttask_link\t")).isTrue();
        }

        @Test
        @DisplayName("should return false for null input")
        void should_return_false_for_null() {
            assertThat(DataResolverType.isValidKey(null)).isFalse();
        }

        @Test
        @DisplayName("should return false for empty string")
        void should_return_false_for_empty_string() {
            assertThat(DataResolverType.isValidKey("")).isFalse();
        }

        @Test
        @DisplayName("should return false for whitespace only")
        void should_return_false_for_whitespace_only() {
            assertThat(DataResolverType.isValidKey("   ")).isFalse();
            assertThat(DataResolverType.isValidKey("\t")).isFalse();
        }

        @Test
        @DisplayName("should return false for uppercase keys (case-sensitive)")
        void should_return_false_for_uppercase_keys() {
            assertThat(DataResolverType.isValidKey("RECIPIENT_FIRSTNAME")).isFalse();
            assertThat(DataResolverType.isValidKey("TASK_LINK")).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid keys")
        void should_return_false_for_invalid_keys() {
            assertThat(DataResolverType.isValidKey("invalid_key")).isFalse();
            assertThat(DataResolverType.isValidKey("firstname")).isFalse();
        }
    }

    // =========================================================================
    // fromKey() METHOD TESTS
    // =========================================================================

    @Nested
    @DisplayName("fromKey() Method Tests")
    class FromKeyMethodTests {

        @Test
        @DisplayName("should return correct enum for valid key")
        void should_return_correct_enum_for_valid_key() {
            assertThat(DataResolverType.fromKey("recipient_firstname"))
                .isEqualTo(DataResolverType.RECIPIENT_FIRSTNAME);
            assertThat(DataResolverType.fromKey("recipient_lastname"))
                .isEqualTo(DataResolverType.RECIPIENT_LASTNAME);
            assertThat(DataResolverType.fromKey("recipient_email"))
                .isEqualTo(DataResolverType.RECIPIENT_EMAIL);
            assertThat(DataResolverType.fromKey("task_link"))
                .isEqualTo(DataResolverType.TASK_LINK);
            assertThat(DataResolverType.fromKey("step_user_name"))
                .isEqualTo(DataResolverType.STEP_USER_NAME);
            assertThat(DataResolverType.fromKey("step_status"))
                .isEqualTo(DataResolverType.STEP_STATUS);
        }

        @Test
        @DisplayName("should return correct enum for keys with spaces")
        void should_return_correct_enum_for_keys_with_spaces() {
            assertThat(DataResolverType.fromKey("  recipient_firstname  "))
                .isEqualTo(DataResolverType.RECIPIENT_FIRSTNAME);
        }

        @Test
        @DisplayName("should return null for null input")
        void should_return_null_for_null() {
            assertThat(DataResolverType.fromKey(null)).isNull();
        }

        @Test
        @DisplayName("should return null for invalid key")
        void should_return_null_for_invalid_key() {
            assertThat(DataResolverType.fromKey("invalid_key")).isNull();
            assertThat(DataResolverType.fromKey("")).isNull();
        }

        @Test
        @DisplayName("should return null for uppercase key (case-sensitive)")
        void should_return_null_for_uppercase_key() {
            assertThat(DataResolverType.fromKey("RECIPIENT_FIRSTNAME")).isNull();
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
            Map<String, String> data = DataResolverType.getAllData();

            assertThat(data).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain correct keys")
        void should_contain_correct_keys() {
            Map<String, String> data = DataResolverType.getAllData();

            assertThat(data.keySet())
                .containsExactlyInAnyOrder(
                    "recipient_firstname",
                    "recipient_lastname",
                    "recipient_email",
                    "task_link",
                    "step_user_name",
                    "step_status"
                );
        }

        @Test
        @DisplayName("should map keys to correct descriptions")
        void should_map_keys_to_correct_descriptions() {
            Map<String, String> data = DataResolverType.getAllData();

            assertThat(data.get("recipient_firstname")).contains("first name");
            assertThat(data.get("task_link")).contains("link");
            assertThat(data.get("step_status")).contains("status");
        }

        @Test
        @DisplayName("should return unmodifiable map")
        void should_return_unmodifiable_map() {
            Map<String, String> data = DataResolverType.getAllData();

            assertThatThrownBy(() -> data.put("NEW_KEY", "New Value"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from map")
        void should_not_allow_removal_from_map() {
            Map<String, String> data = DataResolverType.getAllData();

            assertThatThrownBy(() -> data.remove("recipient_firstname"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on map")
        void should_not_allow_clear_on_map() {
            Map<String, String> data = DataResolverType.getAllData();

            assertThatThrownBy(() -> data.clear())
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should map each key to its enum description")
        void should_map_each_key_to_enum_description() {
            Map<String, String> data = DataResolverType.getAllData();

            for (DataResolverType type : DataResolverType.values()) {
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
            List<String> keys = DataResolverType.getAllKeysList();

            assertThat(keys).hasSize(EXPECTED_ENUM_COUNT);
        }

        @Test
        @DisplayName("should contain all expected keys")
        void should_contain_all_expected_keys() {
            List<String> keys = DataResolverType.getAllKeysList();

            assertThat(keys)
                .containsExactlyInAnyOrder(
                    "recipient_firstname",
                    "recipient_lastname",
                    "recipient_email",
                    "task_link",
                    "step_user_name",
                    "step_status"
                );
        }

        @Test
        @DisplayName("should preserve enum declaration order")
        void should_preserve_enum_declaration_order() {
            List<String> keys = DataResolverType.getAllKeysList();

            assertThat(keys).containsExactly(
                "recipient_firstname",
                "recipient_lastname",
                "recipient_email",
                "task_link",
                "step_user_name",
                "step_status"
            );
        }

        @Test
        @DisplayName("should return unmodifiable list")
        void should_return_unmodifiable_list() {
            List<String> keys = DataResolverType.getAllKeysList();

            assertThatThrownBy(() -> keys.add("NEW_KEY"))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow removal from list")
        void should_not_allow_removal_from_list() {
            List<String> keys = DataResolverType.getAllKeysList();

            assertThatThrownBy(() -> keys.remove(0))
                .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("should not allow clear on list")
        void should_not_allow_clear_on_list() {
            List<String> keys = DataResolverType.getAllKeysList();

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
            assertThat(DataResolverType.valueOf("RECIPIENT_FIRSTNAME"))
                .isEqualTo(DataResolverType.RECIPIENT_FIRSTNAME);
            assertThat(DataResolverType.valueOf("TASK_LINK"))
                .isEqualTo(DataResolverType.TASK_LINK);
            assertThat(DataResolverType.valueOf("STEP_STATUS"))
                .isEqualTo(DataResolverType.STEP_STATUS);
        }

        @Test
        @DisplayName("should throw exception for invalid name")
        void should_throw_exception_for_invalid_name() {
            assertThatThrownBy(() -> DataResolverType.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("should throw exception for null name")
        void should_throw_exception_for_null_name() {
            assertThatThrownBy(() -> DataResolverType.valueOf(null))
                .isInstanceOf(NullPointerException.class);
        }
    }
}
