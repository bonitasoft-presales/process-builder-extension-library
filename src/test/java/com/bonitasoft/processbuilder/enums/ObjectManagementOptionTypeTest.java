package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ObjectsManagementOptionType} enumeration.
 * <p>
 * This class ensures that the enum constants are correctly defined and that the
 * {@code valueOf} method handles both valid and invalid input as expected.
 * </p>
 */
class ObjectsManagementOptionTypeTest {

    /**
     * Tests that the enum has the expected number of constants and that the
     * constant is named correctly.
     */
    @Test
    void enum_should_have_only_one_constant() {
        // Then the enum should have only one constant
        assertEquals(1, ObjectsManagementOptionType.values().length);
        assertEquals("CATEGORY", ObjectsManagementOptionType.CATEGORY.name());
    }

    /**
     * Tests that the {@code valueOf} method returns the correct enum constant
     * for a valid string name.
     */
    @Test
    void valueOf_should_return_correct_enum_constant() {
        // Given a valid string
        String categoryName = "CATEGORY";

        // When getting the enum constant by its name
        ObjectsManagementOptionType enumConstant = ObjectsManagementOptionType.valueOf(categoryName);

        // Then the returned constant should be the CATEGORY enum
        assertEquals(ObjectsManagementOptionType.CATEGORY, enumConstant);
    }

    /**
     * Tests that the {@code valueOf} method throws an {@code IllegalArgumentException}
     * when an invalid string name is provided.
     */
    @Test
    void valueOf_should_throw_exception_for_invalid_constant() {
        // Given an invalid string
        String invalidName = "INVALID_TYPE";

        // When getting the enum constant by its name
        // Then it should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            ObjectsManagementOptionType.valueOf(invalidName);
        });
    }

    /**
     * Tests the {@code isValid} method to ensure it correctly validates all
     * possible input values, including valid, invalid, null, and empty strings.
     */
    @Test
    void isValid_should_correctly_validate_input() {
        // Test with a valid name
        assertTrue(ObjectsManagementOptionType.isValid("CATEGORY"));

        // Test with a valid name in lowercase
        assertTrue(ObjectsManagementOptionType.isValid("category"));

        // Test with a valid name with extra spaces
        assertTrue(ObjectsManagementOptionType.isValid("  CATEGORY  "));

        // Test with a valid name in mixed case
        assertTrue(ObjectsManagementOptionType.isValid("CaTeGoRy"));

        // Test with an invalid name
        assertFalse(ObjectsManagementOptionType.isValid("INVALID_TYPE"));

        // Test with a null string
        assertFalse(ObjectsManagementOptionType.isValid(null));

        // Test with an empty string
        assertFalse(ObjectsManagementOptionType.isValid(""));

        // Test with a blank string
        assertFalse(ObjectsManagementOptionType.isValid(" "));
    }
}