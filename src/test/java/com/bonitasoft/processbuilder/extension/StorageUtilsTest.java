package com.bonitasoft.processbuilder.extension;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link StorageUtils} utility class.
 * This test uses the actual keys defined in the ProcessStorageType enum.
 */
class StorageUtilsTest {

    // --- Tests for isBonitaStorage ---

    @Test
    @DisplayName("isBonitaStorage should return true for 'Bonita'")
    void should_save_to_bonita_for_bonita_key() {
        assertTrue(StorageUtils.isBonitaStorage("Bonita"));
    }

    @Test
    @DisplayName("isBonitaStorage should return true for 'Bonita and delete'")
    void should_save_to_bonita_for_bonita_delete_key() {
        assertTrue(StorageUtils.isBonitaStorage("Bonita and delete"));
    }

    @Test
    @DisplayName("isBonitaStorage should return false for 'Local'")
    void should_not_save_to_bonita_for_local_key() {
        assertFalse(StorageUtils.isBonitaStorage("Local"));
    }
    
    @Test
    @DisplayName("isBonitaStorage should return false for null input")
    void should_save_to_bonita_for_null_input() {
        assertFalse(StorageUtils.isBonitaStorage(null));
    }

    // --- Tests for isLocalStorage ---

    @Test
    @DisplayName("isLocalStorage should return true for 'Local'")
    void should_be_local_for_local_key() {
        assertTrue(StorageUtils.isLocalStorage("Local"));
    }

    @Test
    @DisplayName("isLocalStorage should return true for 'Local and delete'")
    void should_be_local_for_local_delete_key() {
        assertTrue(StorageUtils.isLocalStorage("Local and delete"));
    }

    @Test
    @DisplayName("isLocalStorage should return false for 'Bonita'")
    void should_not_be_local_for_bonita_key() {
        assertFalse(StorageUtils.isLocalStorage("Bonita"));
    }
    
    @Test
    @DisplayName("isLocalStorage should return false for null input")
    void should_be_local_for_null_input() {
        assertFalse(StorageUtils.isLocalStorage(null));
    }

    @Test
    @DisplayName("isBonitaStorage and isLocalStorage should be mutually exclusive")
    void should_be_mutually_exclusive() {
        String bonita = "Bonita";
        assertTrue(StorageUtils.isBonitaStorage(bonita));
        assertFalse(StorageUtils.isLocalStorage(bonita));

        String local = "Local";
        assertFalse(StorageUtils.isBonitaStorage(local));
        assertTrue(StorageUtils.isLocalStorage(local));
    }
}