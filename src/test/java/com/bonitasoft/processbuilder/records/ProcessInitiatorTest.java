package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link ProcessInitiator} record.
 * This class verifies the correct behavior of the record's auto-generated
 * constructor, accessors, equality (hashCode/equals), and toString method.
 */
class ProcessInitiatorTest {

     // Common test data
    private static final Long ID = 101L;
    private static final String USER_NAME = "walter.bates";
    private static final String FULL_NAME = "Walter BAtes";

    /**
     * Test case to verify the correct construction and accessor methods.
     */
    @Test
    @DisplayName("Should correctly instantiate ProcessInitiator and expose all fields via accessors")
    void should_InstantiateAndExposeFields_Correctly() {
        // Given
        ProcessInitiator processInitiator = new ProcessInitiator(
            ID,
            USER_NAME,
            FULL_NAME
        );

        // Then
        assertThat(processInitiator).isNotNull();
        assertThat(processInitiator.id()).isEqualTo(ID);
        assertThat(processInitiator.userName()).isEqualTo(USER_NAME);
        assertThat(processInitiator.fullName()).isEqualTo(FULL_NAME);
    }

    //-------------------------------------------------------------------------
    
    /**
     * Test case to verify the correct behavior of the equals() and hashCode() methods.
     * Records automatically implement these methods based on all component fields.
     */
    @Test
    @DisplayName("Should correctly implement equals and hashCode for equal and different instances")
    void should_ImplementEqualsAndHashCode_Correctly() {
        // Given
        ProcessInitiator processInitiator1 = new ProcessInitiator(
            ID, USER_NAME, FULL_NAME
        );
        ProcessInitiator processInitiator2 = new ProcessInitiator(
            ID, USER_NAME, FULL_NAME
        );

        // A different instance (only changing the ID)
        ProcessInitiator differentProcessInitiator = new ProcessInitiator(
            999L, USER_NAME, FULL_NAME
        );

        // Then
        // 1. Instances with the same field values should be equal and have the same hash code
        assertThat(processInitiator1)
            .isEqualTo(processInitiator2)
            .hasSameHashCodeAs(processInitiator2);

        // 2. An instance should not be equal to a different instance
        assertThat(processInitiator1).isNotEqualTo(differentProcessInitiator);
        
        // 3. Hash codes of different instances are usually different (though not guaranteed)
        assertThat(processInitiator1.hashCode()).isNotEqualTo(differentProcessInitiator.hashCode());
        
        // 4. Record should not be equal to null or another class
        assertThat(processInitiator1).isNotEqualTo(null);
        assertThat(processInitiator1).isNotEqualTo(new Object());
    }

    //-------------------------------------------------------------------------

    /**
     * Test case to verify the generated toString() method, which is useful for logging.
     */
    @Test
    @DisplayName("Should generate a useful toString representation")
    void should_Generate_ToString() {
        // Given
        ProcessInitiator processInitiator = new ProcessInitiator(
            ID,
            USER_NAME,
            FULL_NAME
        );

        
        // When
        String result = processInitiator.toString();

        // Then
        // Records typically format toString as: ClassName[field1=value1, field2=value2, ...]
        assertThat(result)
            .contains("ProcessInitiator[")
            .contains("id=" + ID)
            .contains("userName=" + USER_NAME)
            .contains("fullName=" + FULL_NAME)
            .endsWith("]");
    }
}