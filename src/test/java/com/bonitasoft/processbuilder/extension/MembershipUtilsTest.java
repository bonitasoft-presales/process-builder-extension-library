package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for MembershipUtils to ensure full coverage of the buildMembershipKey method.
 */
public class MembershipUtilsTest {

    // Test case 1: Both IDs are valid (Key constructed)
    @Test
    void shouldBuildKeyWhenBothIdsAreValid() {
        Long groupId = 123L;
        Long roleId = 456L;
        String expected = "123$456";
        String actual = MembershipUtils.buildMembershipKey(groupId, roleId);
        assertEquals(expected, actual, "The key should be correctly concatenated with '$'.");
    }

    // Test case 2: Group ID is null (Expected: null)
    @Test
    void shouldReturnNullWhenGroupIdIsNull() {
        Long groupId = null;
        Long roleId = 456L;
        String actual = MembershipUtils.buildMembershipKey(groupId, roleId);
        assertNull(actual, "Should return null when groupId is null."); // <-- CAMBIO AQUÍ
    }

    // Test case 3: Role ID is null (Expected: null)
    @Test
    void shouldReturnNullWhenRoleIdIsNull() {
        Long groupId = 123L;
        Long roleId = null;
        String actual = MembershipUtils.buildMembershipKey(groupId, roleId);
        assertNull(actual, "Should return null when roleId is null."); // <-- CAMBIO AQUÍ
    }

    // Test case 4: Both IDs are null (Expected: null)
    @Test
    void shouldReturnNullWhenBothIdsAreNull() {
        Long groupId = null;
        Long roleId = null;
        String actual = MembershipUtils.buildMembershipKey(groupId, roleId);
        assertNull(actual, "Should return null when both IDs are null."); // <-- CAMBIO AQUÍ
    }
    
    // Test case 5: Edge case with maximum Long values
    @Test
    void shouldHandleLargeLongValues() {
        Long groupId = 9223372036854775807L;
        Long roleId = 1L;
        String expected = "9223372036854775807$1";
        String actual = MembershipUtils.buildMembershipKey(groupId, roleId);
        assertEquals(expected, actual, "Should handle the maximum Long value correctly.");
    }
}