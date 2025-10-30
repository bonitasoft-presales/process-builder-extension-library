package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for the {@link ActionType} enumeration.
 * <p>
 * This class validates the behavior of the {@code isValid} method for each
 * {@link ActionType} constant, ensuring the correct validation logic for
 * different types of persistence IDs.
 * </p>
 */
class ActionTypeTest {

    /**
     * Tests that the {@code isValid} method returns true for valid {@code INSERT} action IDs.
     * A valid persistence ID for an {@code INSERT} is an empty or blank string.
     */
    @Test
    void isValid_should_return_true_for_valid_insert() {
        // Given an empty or blank persistence ID string
        String emptyId = "";
        String blankId = "   ";

        // When validating against INSERT action
        boolean emptyIsValid = ActionType.INSERT.isValid(emptyId);
        boolean blankIsValid = ActionType.INSERT.isValid(blankId);

        // Then it should return true
        assertTrue(emptyIsValid);
        assertTrue(blankIsValid);
    }

    /**
     * Tests that the {@code isValid} method returns false for invalid {@code INSERT} action IDs.
     * An invalid ID for an {@code INSERT} is a non-empty string.
     */
    @Test
    void isValid_should_return_false_for_invalid_insert() {
        // Given a non-empty, non-blank persistence ID string
        String invalidId = "123";

        // When validating against INSERT action
        boolean isValid = ActionType.INSERT.isValid(invalidId);

        // Then it should return false
        assertFalse(isValid);
    }

    /**
     * Tests that the {@code isValid} method returns true for a valid {@code UPDATE} action ID.
     * A valid ID for an {@code UPDATE} is a non-empty string containing only digits.
     */
    @Test
    void isValid_should_return_true_for_valid_update() {
        // Given a non-empty persistence ID string with only digits
        String validId = "456";

        // When validating against UPDATE action
        boolean isValid = ActionType.UPDATE.isValid(validId);

        // Then it should return true
        assertTrue(isValid);
    }

    /**
     * Tests that the {@code isValid} method returns false for invalid {@code UPDATE} action IDs.
     * Invalid IDs include empty, non-digit, or mixed strings.
     */
    @Test
    void isValid_should_return_false_for_invalid_update() {
        // Given invalid persistence ID strings
        String emptyId = "";
        String nonDigitId = "abc";
        String mixedId = "123a";

        // When validating against UPDATE action
        boolean emptyIsValid = ActionType.UPDATE.isValid(emptyId);
        boolean nonDigitIsValid = ActionType.UPDATE.isValid(nonDigitId);
        boolean mixedIsValid = ActionType.UPDATE.isValid(mixedId);

        // Then it should return false
        assertFalse(emptyIsValid);
        assertFalse(nonDigitIsValid);
        assertFalse(mixedIsValid);
    }

    /**
     * Tests that the {@code isValid} method returns true for a valid {@code DELETE} action ID.
     * A valid ID for a {@code DELETE} is a non-empty string containing only digits.
     */
    @Test
    void isValid_should_return_true_for_valid_delete() {
        // Given a non-empty persistence ID string with only digits
        String validId = "789";

        // When validating against DELETE action
        boolean isValid = ActionType.DELETE.isValid(validId);

        // Then it should return true
        assertTrue(isValid);
    }

    /**
     * Tests that the {@code isValid} method returns false for invalid {@code DELETE} action IDs.
     * Invalid IDs include empty, non-digit, or mixed strings.
     */
    @Test
    void isValid_should_return_false_for_invalid_delete() {
        // Given invalid persistence ID strings
        String emptyId = "";
        String nonDigitId = "xyz";
        String mixedId = "456b";

        // When validating against DELETE action
        boolean emptyIsValid = ActionType.DELETE.isValid(emptyId);
        boolean nonDigitIsValid = ActionType.DELETE.isValid(nonDigitId);
        boolean mixedIsValid = ActionType.DELETE.isValid(mixedId);

        // Then it should return false
        assertFalse(emptyIsValid);
        assertFalse(nonDigitIsValid);
        assertFalse(mixedIsValid);
    }

    @Test
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ActionType.getAllData();
        assertEquals(3, data.size());
        assertTrue(data.containsKey("Insert"));
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
    }

    @Test
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ActionType.getAllKeysList();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("Update"));
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
    }
}