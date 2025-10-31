package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link TaskExecutor} record.
 * This class verifies the correct behavior of the record's auto-generated
 * constructor, accessors, equality (hashCode/equals), and toString method.
 */
class TaskExecutorTest {

    // Common test data
    private static final Long ID = 101L;
    private static final String USER_NAME = "walter.bates";
    private static final String FULL_NAME = "Walter BAtes";

    /**
     * Test case to verify the correct construction and accessor methods.
     */
    @Test
    @DisplayName("Should correctly instantiate TaskExecutor and expose all fields via accessors")
    void should_InstantiateAndExposeFields_Correctly() {
        // Given
        TaskExecutor taskExecutor = new TaskExecutor(
            ID,
            USER_NAME,
            FULL_NAME
        );

        // Then
        assertThat(taskExecutor).isNotNull();
        assertThat(taskExecutor.id()).isEqualTo(ID);
        assertThat(taskExecutor.userName()).isEqualTo(USER_NAME);
        assertThat(taskExecutor.fullName()).isEqualTo(FULL_NAME);
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
        TaskExecutor taskExecutor1 = new TaskExecutor(
            ID, USER_NAME, FULL_NAME
        );
        TaskExecutor taskExecutor2 = new TaskExecutor(
            ID, USER_NAME, FULL_NAME
        );

        // A different instance (only changing the ID)
        TaskExecutor differentTaskExecutor = new TaskExecutor(
            999L, USER_NAME, FULL_NAME
        );

        // Then
        // 1. Instances with the same field values should be equal and have the same hash code
        assertThat(taskExecutor1)
            .isEqualTo(taskExecutor2)
            .hasSameHashCodeAs(taskExecutor2);

        // 2. An instance should not be equal to a different instance
        assertThat(taskExecutor1).isNotEqualTo(differentTaskExecutor);
        
        // 3. Hash codes of different instances are usually different (though not guaranteed)
        assertThat(taskExecutor1.hashCode()).isNotEqualTo(differentTaskExecutor.hashCode());
        
        // 4. Record should not be equal to null or another class
        assertThat(taskExecutor1).isNotEqualTo(null);
        assertThat(taskExecutor1).isNotEqualTo(new Object());
    }

    //-------------------------------------------------------------------------

    /**
     * Test case to verify the generated toString() method, which is useful for logging.
     */
    @Test
    @DisplayName("Should generate a useful toString representation")
    void should_Generate_ToString() {
        // Given
        TaskExecutor taskExecutor = new TaskExecutor(
            ID,
            USER_NAME,
            FULL_NAME
        );

        
        // When
        String result = taskExecutor.toString();

        // Then
        // Records typically format toString as: ClassName[field1=value1, field2=value2, ...]
        assertThat(result)
            .contains("TaskExecutor[")
            .contains("id=" + ID)
            .contains("userName=" + USER_NAME)
            .contains("fullName=" + FULL_NAME)
            .endsWith("]");
    }
}