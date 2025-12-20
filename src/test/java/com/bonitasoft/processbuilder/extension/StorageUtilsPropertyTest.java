package com.bonitasoft.processbuilder.extension;

import com.bonitasoft.processbuilder.enums.ProcessStorageType;
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link StorageUtils} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("StorageUtils Property-Based Tests")
class StorageUtilsPropertyTest {

    // =========================================================================
    // UTILITY CLASS INVARIANT PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Utility class should not be instantiable")
    void utilityClassShouldNotBeInstantiable() {
        assertThatThrownBy(() -> {
            var constructor = StorageUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).hasCauseInstanceOf(UnsupportedOperationException.class);
    }

    // =========================================================================
    // isBonitaStorage() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isBonitaStorage should return false for null")
    void isBonitaStorageShouldReturnFalseForNull() {
        assertThat(StorageUtils.isBonitaStorage(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isBonitaStorage should return true for BONITA key")
    void isBonitaStorageShouldReturnTrueForBonitaKey() {
        String bonitaKey = ProcessStorageType.BONITA.getKey();
        assertThat(StorageUtils.isBonitaStorage(bonitaKey)).isTrue();
    }

    @Property(tries = 100)
    @Label("isBonitaStorage should return true for BONITA_AND_DELETE key")
    void isBonitaStorageShouldReturnTrueForBonitaAndDeleteKey() {
        String bonitaAndDeleteKey = ProcessStorageType.BONITA_AND_DELETE.getKey();
        assertThat(StorageUtils.isBonitaStorage(bonitaAndDeleteKey)).isTrue();
    }

    @Property(tries = 100)
    @Label("isBonitaStorage should return false for LOCAL key")
    void isBonitaStorageShouldReturnFalseForLocalKey() {
        String localKey = ProcessStorageType.LOCAL.getKey();
        assertThat(StorageUtils.isBonitaStorage(localKey)).isFalse();
    }

    @Property(tries = 100)
    @Label("isBonitaStorage should return false for LOCAL_AND_DELETE key")
    void isBonitaStorageShouldReturnFalseForLocalAndDeleteKey() {
        String localAndDeleteKey = ProcessStorageType.LOCAL_AND_DELETE.getKey();
        assertThat(StorageUtils.isBonitaStorage(localAndDeleteKey)).isFalse();
    }

    @Property(tries = 200)
    @Label("isBonitaStorage should return false for random strings")
    void isBonitaStorageShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 10, max = 50) @AlphaChars String randomKey) {
        // Random alpha strings should not match storage keys
        assertThat(StorageUtils.isBonitaStorage(randomKey)).isFalse();
    }

    // =========================================================================
    // isLocalStorage() PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isLocalStorage should return false for null")
    void isLocalStorageShouldReturnFalseForNull() {
        assertThat(StorageUtils.isLocalStorage(null)).isFalse();
    }

    @Property(tries = 100)
    @Label("isLocalStorage should return true for LOCAL key")
    void isLocalStorageShouldReturnTrueForLocalKey() {
        String localKey = ProcessStorageType.LOCAL.getKey();
        assertThat(StorageUtils.isLocalStorage(localKey)).isTrue();
    }

    @Property(tries = 100)
    @Label("isLocalStorage should return true for LOCAL_AND_DELETE key")
    void isLocalStorageShouldReturnTrueForLocalAndDeleteKey() {
        String localAndDeleteKey = ProcessStorageType.LOCAL_AND_DELETE.getKey();
        assertThat(StorageUtils.isLocalStorage(localAndDeleteKey)).isTrue();
    }

    @Property(tries = 100)
    @Label("isLocalStorage should return false for BONITA key")
    void isLocalStorageShouldReturnFalseForBonitaKey() {
        String bonitaKey = ProcessStorageType.BONITA.getKey();
        assertThat(StorageUtils.isLocalStorage(bonitaKey)).isFalse();
    }

    @Property(tries = 100)
    @Label("isLocalStorage should return false for BONITA_AND_DELETE key")
    void isLocalStorageShouldReturnFalseForBonitaAndDeleteKey() {
        String bonitaAndDeleteKey = ProcessStorageType.BONITA_AND_DELETE.getKey();
        assertThat(StorageUtils.isLocalStorage(bonitaAndDeleteKey)).isFalse();
    }

    @Property(tries = 200)
    @Label("isLocalStorage should return false for random strings")
    void isLocalStorageShouldReturnFalseForRandomStrings(
            @ForAll @StringLength(min = 10, max = 50) @AlphaChars String randomKey) {
        // Random alpha strings should not match storage keys
        assertThat(StorageUtils.isLocalStorage(randomKey)).isFalse();
    }

    // =========================================================================
    // MUTUAL EXCLUSION PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("Bonita and Local storage should be mutually exclusive for BONITA types")
    void bonitaAndLocalShouldBeMutuallyExclusiveForBonita() {
        for (ProcessStorageType type : new ProcessStorageType[]{
                ProcessStorageType.BONITA, ProcessStorageType.BONITA_AND_DELETE}) {
            String key = type.getKey();
            assertThat(StorageUtils.isBonitaStorage(key)).isTrue();
            assertThat(StorageUtils.isLocalStorage(key)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("Bonita and Local storage should be mutually exclusive for LOCAL types")
    void bonitaAndLocalShouldBeMutuallyExclusiveForLocal() {
        for (ProcessStorageType type : new ProcessStorageType[]{
                ProcessStorageType.LOCAL, ProcessStorageType.LOCAL_AND_DELETE}) {
            String key = type.getKey();
            assertThat(StorageUtils.isLocalStorage(key)).isTrue();
            assertThat(StorageUtils.isBonitaStorage(key)).isFalse();
        }
    }

    @Property(tries = 100)
    @Label("Null should return false for both storage checks")
    void nullShouldReturnFalseForBoth() {
        assertThat(StorageUtils.isBonitaStorage(null)).isFalse();
        assertThat(StorageUtils.isLocalStorage(null)).isFalse();
    }

    @Property(tries = 200)
    @Label("Random strings should return false for both storage checks")
    void randomStringsShouldReturnFalseForBoth(
            @ForAll @StringLength(min = 10, max = 50) @AlphaChars String randomKey) {
        assertThat(StorageUtils.isBonitaStorage(randomKey)).isFalse();
        assertThat(StorageUtils.isLocalStorage(randomKey)).isFalse();
    }

    // =========================================================================
    // CASE INSENSITIVITY PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("isBonitaStorage should be case insensitive")
    void isBonitaStorageShouldBeCaseInsensitive() {
        // Test various case variations
        assertThat(StorageUtils.isBonitaStorage("Bonita")).isTrue();
        assertThat(StorageUtils.isBonitaStorage("BONITA")).isTrue();
        assertThat(StorageUtils.isBonitaStorage("bonita")).isTrue();
        assertThat(StorageUtils.isBonitaStorage("Bonita and delete")).isTrue();
        assertThat(StorageUtils.isBonitaStorage("BONITA AND DELETE")).isTrue();
    }

    @Property(tries = 100)
    @Label("isLocalStorage should be case insensitive")
    void isLocalStorageShouldBeCaseInsensitive() {
        // Test various case variations
        assertThat(StorageUtils.isLocalStorage("Local")).isTrue();
        assertThat(StorageUtils.isLocalStorage("LOCAL")).isTrue();
        assertThat(StorageUtils.isLocalStorage("local")).isTrue();
        assertThat(StorageUtils.isLocalStorage("Local and delete")).isTrue();
        assertThat(StorageUtils.isLocalStorage("LOCAL AND DELETE")).isTrue();
    }
}
