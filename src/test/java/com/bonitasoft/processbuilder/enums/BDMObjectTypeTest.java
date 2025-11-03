package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link BDMObjectType} enumeration.
 * <p>
 * This class validates the behavior of the {@code BDMObjectType} enum,
 * ensuring correct key/description retrieval, utility methods, and edge cases.
 * </p>
 */
class BDMObjectTypeTest {

    // =========================================================================
    // SECTION 1: getKey() and getDescription() Tests
    // =========================================================================

    @Test
    @DisplayName("getKey should return correct key for PB_PROCESS_INSTANCE")
    void getKey_should_return_correct_key_for_pb_process_instance() {
        // When
        String key = BDMObjectType.PB_PROCESS_INSTANCE.getKey();

        // Then
        assertEquals("PBProcessInstance", key);
    }

    @Test
    @DisplayName("getKey should return correct key for PB_ACTION")
    void getKey_should_return_correct_key_for_pb_action() {
        // When
        String key = BDMObjectType.PB_ACTION.getKey();

        // Then
        assertEquals("PBAction", key);
    }

    @Test
    @DisplayName("getKey should return correct key for PB_CATEGORY")
    void getKey_should_return_correct_key_for_pb_category() {
        // When
        String key = BDMObjectType.PB_CATEGORY.getKey();

        // Then
        assertEquals("PBCategory", key);
    }

    @Test
    @DisplayName("getKey should return correct key for all enum constants")
    void getKey_should_return_correct_key_for_all_types() {
        // When/Then: Iterate over all enum values and verify symmetry
        for (BDMObjectType type : BDMObjectType.values()) {
            String key = type.getKey();
            
            // Verify the key is not null and not empty
            assertNotNull(key, "getKey() should not return null for " + type.name());
            assertFalse(key.isEmpty(), "getKey() should not be empty for " + type.name());
            
            // Verify fromKey can retrieve the same enum
            BDMObjectType retrievedType = BDMObjectType.fromKey(key);
            assertEquals(type, retrievedType, 
                        "fromKey() should return the same enum for key: " + key);
        }
    }

    @Test
    @DisplayName("getDescription should return non-empty description for PB_PROCESS_INSTANCE")
    void getDescription_should_return_non_empty_for_pb_process_instance() {
        // When
        String description = BDMObjectType.PB_PROCESS_INSTANCE.getDescription();

        // Then
        assertNotNull(description);
        assertFalse(description.isEmpty());
        assertTrue(description.contains("Process Instance"));
    }

    @Test
    @DisplayName("getDescription should return non-empty description for all enum constants")
    void getDescription_should_return_non_empty_for_all_types() {
        // When/Then
        for (BDMObjectType type : BDMObjectType.values()) {
            String description = type.getDescription();
            assertNotNull(description, "Description should not be null for " + type.name());
            assertFalse(description.isEmpty(), "Description should not be empty for " + type.name());
            assertTrue(description.length() > 10, "Description should be meaningful for " + type.name());
        }
    }

    // =========================================================================
    // SECTION 2: toString() Tests
    // =========================================================================

    @Test
    @DisplayName("toString should return formatted string with enum name, key, and description")
    void toString_should_return_formatted_string() {
        // When
        String result = BDMObjectType.PB_ACTION.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("PB_ACTION"));
        assertTrue(result.contains("PBAction"));
        assertTrue(result.contains("description"));
    }

    @Test
    @DisplayName("toString should return different values for different enum constants")
    void toString_should_return_different_values_for_different_types() {
        // When
        String actionString = BDMObjectType.PB_ACTION.toString();
        String processString = BDMObjectType.PB_PROCESS.toString();

        // Then
        assertNotEquals(actionString, processString);
    }

    // =========================================================================
    // SECTION 3: getAllData() Tests
    // =========================================================================

    @Test
    @DisplayName("getAllData should return map with all 13 BDM object types")
    void getAllData_should_return_map_with_all_types() {
        // When
        Map<String, String> data = BDMObjectType.getAllData();

        // Then
        assertEquals(13, data.size());
    }

    @Test
    @DisplayName("getAllData should contain all expected keys")
    void getAllData_should_contain_all_expected_keys() {
        // When
        Map<String, String> data = BDMObjectType.getAllData();

        // Then
        assertTrue(data.containsKey("PBDataProcessInstance"));
        assertTrue(data.containsKey("PBProcessInstance"));
        assertTrue(data.containsKey("PBStepProcessInstance"));
        assertTrue(data.containsKey("PBAction"));
        assertTrue(data.containsKey("PBCategory"));
        assertTrue(data.containsKey("PBEntityType"));
        assertTrue(data.containsKey("PBFiles"));
        assertTrue(data.containsKey("PBGenericEntry"));
        assertTrue(data.containsKey("PBProcess"));
        assertTrue(data.containsKey("PBRunningInstance"));
        assertTrue(data.containsKey("PBSmtp"));
        assertTrue(data.containsKey("PBSteps"));
        assertTrue(data.containsKey("PBUserList"));
    }

    @Test
    @DisplayName("getAllData should return unmodifiable map")
    void getAllData_should_return_unmodifiable_map() {
        // When
        Map<String, String> data = BDMObjectType.getAllData();

        // Then
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("NewKey", "NewValue"));
        assertThrows(UnsupportedOperationException.class, () -> data.remove("PBAction"));
    }

    @Test
    @DisplayName("getAllData should map each key to its correct description")
    void getAllData_should_map_keys_to_correct_descriptions() {
        // When
        Map<String, String> data = BDMObjectType.getAllData();

        // Then
        for (BDMObjectType type : BDMObjectType.values()) {
            assertTrue(data.containsKey(type.getKey()));
            assertEquals(type.getDescription(), data.get(type.getKey()));
        }
    }

    // =========================================================================
    // SECTION 4: getAllKeysList() Tests
    // =========================================================================

    @Test
    @DisplayName("getAllKeysList should return list with all 13 keys")
    void getAllKeysList_should_return_list_with_all_keys() {
        // When
        List<String> keys = BDMObjectType.getAllKeysList();

        // Then
        assertEquals(13, keys.size());
    }

    @Test
    @DisplayName("getAllKeysList should contain all expected keys")
    void getAllKeysList_should_contain_all_expected_keys() {
        // When
        List<String> keys = BDMObjectType.getAllKeysList();

        // Then
        assertTrue(keys.contains("PBAction"));
        assertTrue(keys.contains("PBProcess"));
        assertTrue(keys.contains("PBCategory"));
        assertTrue(keys.contains("PBProcessInstance"));
        assertTrue(keys.contains("PBSteps"));
    }

    @Test
    @DisplayName("getAllKeysList should return unmodifiable list")
    void getAllKeysList_should_return_unmodifiable_list() {
        // When
        List<String> keys = BDMObjectType.getAllKeysList();

        // Then
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NewKey"));
        assertThrows(UnsupportedOperationException.class, () -> keys.clear());
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }

    // =========================================================================
    // SECTION 5: fromKey() Tests
    // =========================================================================

    @Test
    @DisplayName("fromKey should return correct enum for valid key 'PBProcess'")
    void fromKey_should_return_correct_enum_for_pb_process() {
        // When
        BDMObjectType result = BDMObjectType.fromKey("PBProcess");

        // Then
        assertEquals(BDMObjectType.PB_PROCESS, result);
    }

    @Test
    @DisplayName("fromKey should return correct enum for valid key 'PBAction'")
    void fromKey_should_return_correct_enum_for_pb_action() {
        // When
        BDMObjectType result = BDMObjectType.fromKey("PBAction");

        // Then
        assertEquals(BDMObjectType.PB_ACTION, result);
    }

    @Test
    @DisplayName("fromKey should return correct enum for all valid keys")
    void fromKey_should_return_correct_enum_for_all_valid_keys() {
        // Given all enum values
        for (BDMObjectType expectedType : BDMObjectType.values()) {
            // When
            BDMObjectType result = BDMObjectType.fromKey(expectedType.getKey());

            // Then
            assertEquals(expectedType, result);
        }
    }

    @Test
    @DisplayName("fromKey should be case-insensitive")
    void fromKey_should_be_case_insensitive() {
        // When
        BDMObjectType result1 = BDMObjectType.fromKey("pbprocess");
        BDMObjectType result2 = BDMObjectType.fromKey("PBPROCESS");
        BDMObjectType result3 = BDMObjectType.fromKey("PbPrOcEsS");

        // Then
        assertEquals(BDMObjectType.PB_PROCESS, result1);
        assertEquals(BDMObjectType.PB_PROCESS, result2);
        assertEquals(BDMObjectType.PB_PROCESS, result3);
    }

    @Test
    @DisplayName("fromKey should throw IllegalArgumentException for invalid key")
    void fromKey_should_throw_exception_for_invalid_key() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> BDMObjectType.fromKey("InvalidKey"));
    }

    @Test
    @DisplayName("fromKey should throw IllegalArgumentException for null key")
    void fromKey_should_throw_exception_for_null_key() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> BDMObjectType.fromKey(null));
    }

    @Test
    @DisplayName("fromKey should throw IllegalArgumentException for empty key")
    void fromKey_should_throw_exception_for_empty_key() {
        // When/Then
        assertThrows(IllegalArgumentException.class, () -> BDMObjectType.fromKey(""));
    }

    @Test
    @DisplayName("fromKey exception message should contain valid keys list")
    void fromKey_exception_message_should_contain_valid_keys() {
        // When
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> BDMObjectType.fromKey("NonExistentKey")
        );

        // Then
        String message = exception.getMessage();
        assertNotNull(message);
        assertTrue(message.contains("NonExistentKey"));
        assertTrue(message.contains("Valid keys"));
    }

    // =========================================================================
    // SECTION 6: Enum Constants Validation
    // =========================================================================

    @Test
    @DisplayName("Verify all 13 enum constants exist")
    void verify_all_enum_constants_exist() {
        // When/Then
        assertEquals(13, BDMObjectType.values().length);
        
        // Verify each constant
        assertNotNull(BDMObjectType.PB_DATA_PROCESS_INSTANCE);
        assertNotNull(BDMObjectType.PB_PROCESS_INSTANCE);
        assertNotNull(BDMObjectType.PB_STEP_PROCESS_INSTANCE);
        assertNotNull(BDMObjectType.PB_ACTION);
        assertNotNull(BDMObjectType.PB_CATEGORY);
        assertNotNull(BDMObjectType.PB_ENTITY_TYPE);
        assertNotNull(BDMObjectType.PB_FILES);
        assertNotNull(BDMObjectType.PB_GENERIC_ENTRY);
        assertNotNull(BDMObjectType.PB_PROCESS);
        assertNotNull(BDMObjectType.PB_RUNNING_INSTANCE);
        assertNotNull(BDMObjectType.PB_SMTP);
        assertNotNull(BDMObjectType.PB_STEPS);
        assertNotNull(BDMObjectType.PB_USER_LIST);
    }

    @Test
    @DisplayName("Each enum constant should have unique key")
    void each_enum_constant_should_have_unique_key() {
        // When/Then
        BDMObjectType[] values = BDMObjectType.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals(
                    values[i].getKey(), 
                    values[j].getKey(),
                    "Keys must be unique: " + values[i].getKey() + " vs " + values[j].getKey()
                );
            }
        }
    }

    @Test
    @DisplayName("Each enum constant should have non-empty description")
    void each_enum_constant_should_have_non_empty_description() {
        // When/Then
        for (BDMObjectType type : BDMObjectType.values()) {
            assertNotNull(type.getDescription());
            assertFalse(type.getDescription().isEmpty());
        }
    }

    // =========================================================================
    // SECTION 7: Edge Cases and Integration Tests
    // =========================================================================

    @Test
    @DisplayName("fromKey and getKey should be symmetric")
    void fromKey_and_getKey_should_be_symmetric() {
        // When/Then
        for (BDMObjectType type : BDMObjectType.values()) {
            String key = type.getKey();
            BDMObjectType retrieved = BDMObjectType.fromKey(key);
            assertEquals(type, retrieved);
        }
    }

    @Test
    @DisplayName("getAllData keys should match getAllKeysList contents")
    void getAllData_keys_should_match_getAllKeysList() {
        // When
        Map<String, String> data = BDMObjectType.getAllData();
        List<String> keys = BDMObjectType.getAllKeysList();

        // Then
        assertEquals(data.keySet().size(), keys.size());
        assertTrue(keys.containsAll(data.keySet()));
        assertTrue(data.keySet().containsAll(keys));
    }

    @Test
    @DisplayName("getAllKeysList should contain same keys as enum constants")
    void getAllKeysList_should_contain_same_keys_as_enum_constants() {
        // When
        List<String> keys = BDMObjectType.getAllKeysList();

        // Then
        for (BDMObjectType type : BDMObjectType.values()) {
            assertTrue(keys.contains(type.getKey()));
        }
    }
}