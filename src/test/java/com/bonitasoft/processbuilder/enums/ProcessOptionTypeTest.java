package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ProcessOptionType} enumeration.
 * <p>
 * This class validates that the enum contains all expected constants and that
 * the {@code valueOf} method works as expected for both valid and invalid inputs.
 * </p>
 */
class ProcessOptionTypeTest {

    /**
     * Tests that the enum contains the expected number of constants and that
     * all specific constants are present.
     */
    @Test
    void enum_should_contain_all_expected_constants() {
        // Given the array of enum values
        ProcessOptionType[] types = ProcessOptionType.values();

        // Then the enum should contain the expected number of constants
        assertEquals(5, types.length);
        
        // And it should contain all specific constants
        assertTrue(containsEnum(types, "PARAMETER"));
        assertTrue(containsEnum(types, "USERS"));
        assertTrue(containsEnum(types, "INPUTS"));
        assertTrue(containsEnum(types, "STEPS"));
        assertTrue(containsEnum(types, "STATUS"));
    }

    /**
     * Tests that the {@code valueOf} method returns the correct enum constant
     * for a valid string name.
     */
    @Test
    void valueOf_should_return_correct_enum_constant() {
        // Given a valid string
        String usersName = "USERS";

        // When getting the enum constant by its name
        ProcessOptionType enumConstant = ProcessOptionType.valueOf(usersName);

        // Then the returned constant should be the USERS enum
        assertEquals(ProcessOptionType.USERS, enumConstant);
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
            ProcessOptionType.valueOf(invalidName);
        });
    }
    
    /**
     * Helper method to check if an array of enums contains a specific constant name.
     *
     * @param enums An array of {@link ProcessOptionType} enums.
     * @param name The name of the enum constant to check for.
     * @return {@code true} if the name exists in the enum array, otherwise {@code false}.
     */
    private boolean containsEnum(ProcessOptionType[] enums, String name) {
        for (ProcessOptionType e : enums) {
            if (e.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}