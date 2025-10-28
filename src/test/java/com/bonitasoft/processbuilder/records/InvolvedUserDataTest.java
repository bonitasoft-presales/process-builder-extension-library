package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Robust test class for the InvolvedUsersData record, focusing on data integrity and immutability
 * of the membership list, and testing the {@code getRefStepsArray} method for correct filtering logic.
 */
class InvolvedUsersDataTest {

    private static final String MANAGER_REF = "Step_Start_Manager";
    private static final String USER_REF = "Step_Review_User";
    private List<String> mutableMemberships;
    private InvolvedUsersData involvedUsersData;

    @BeforeEach
    void setUp() {
        // Initialize mutable list before each test
        mutableMemberships = new ArrayList<>(List.of("Role_HR", "Group_Dev"));
        
        // This is the full valid data object used by the accessor tests
        involvedUsersData = new InvolvedUsersData(
                MANAGER_REF, 
                USER_REF, 
                mutableMemberships
        );
    }
    
    // ------------------- Basic Value Verification Tests -------------------

    @Test
    void shouldReturnCorrectScalarValues() {
        // Assert scalar values are correctly set
        assertThat(involvedUsersData.stepManagerRef()).isEqualTo(MANAGER_REF);
        assertThat(involvedUsersData.stepUserRef()).isEqualTo(USER_REF);
    }
    
    @Test
    void shouldContainAllMembershipElements() {
        // Assert list contents are correct
        assertThat(involvedUsersData.memberships()).containsExactlyInAnyOrderElementsOf(mutableMemberships);
    }

    // ------------------- Immutability (Defensive Copy) Tests -------------------

    @Test
    void constructorShouldCreateDefensiveCopyOfMemberships() {
        // WHEN the external mutable list is changed AFTER construction
        mutableMemberships.add("Role_Marketing");

        // THEN the internal state of the record must remain unchanged
        assertThat(involvedUsersData.memberships()).hasSize(2)
                .containsExactly("Role_HR", "Group_Dev");
    }

    @Test
    void membershipsAccessorShouldReturnUnmodifiableList() {
        List<String> returnedList = involvedUsersData.memberships();
        
        // 1. Assert list is unmodifiable
        assertThatThrownBy(() -> returnedList.add("Invalid_Add"))
            .isInstanceOf(UnsupportedOperationException.class);
            
        // 2. Assert that modification attempt does not affect the internal state
        assertThat(involvedUsersData.memberships()).hasSize(2);
    }

    @Test
    void shouldHandleEmptyLists() {
        // Test case with empty list
        InvolvedUsersData emptyData = new InvolvedUsersData(
                "NoRef", 
                "NoRef", 
                Collections.emptyList()
        );
        
        // Assertions for the empty case
        assertThat(emptyData.memberships()).isNotNull().isEmpty();
        
        // Assert modification still fails on the returned empty list
        assertThatThrownBy(() -> emptyData.memberships().add("Test"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
    
    // ------------------- getRefStepsArray() Tests -------------------

    @Test
    @DisplayName("getRefStepsArray should return both references when both are valid strings")
    void getRefStepsArray_BothValid() {
        String[] result = involvedUsersData.getRefStepsArray();
        
        assertThat(result)
            .containsExactlyInAnyOrder(MANAGER_REF, USER_REF)
            .hasSize(2);
    }
    
    @Test
    @DisplayName("getRefStepsArray should filter out null references")
    void getRefStepsArray_FiltersNull() {
        InvolvedUsersData data = new InvolvedUsersData(
            MANAGER_REF, 
            null, // stepUserRef is null
            Collections.emptyList()
        );
        
        String[] result = data.getRefStepsArray();
        
        assertThat(result)
            .containsExactly(MANAGER_REF)
            .hasSize(1);
    }
    
    @Test
    @DisplayName("getRefStepsArray should filter out empty string references")
    void getRefStepsArray_FiltersEmptyString() {
        InvolvedUsersData data = new InvolvedUsersData(
            "", // stepManagerRef is empty
            USER_REF, 
            Collections.emptyList()
        );
        
        String[] result = data.getRefStepsArray();
        
        assertThat(result)
            .containsExactly(USER_REF)
            .hasSize(1);
    }

    @Test
    @DisplayName("getRefStepsArray should filter out whitespace string references")
    void getRefStepsArray_FiltersWhitespace() {
        InvolvedUsersData data = new InvolvedUsersData(
            MANAGER_REF, 
            "  \t", // stepUserRef is whitespace
            Collections.emptyList()
        );
        
        String[] result = data.getRefStepsArray();
        
        assertThat(result)
            .containsExactly(MANAGER_REF)
            .hasSize(1);
    }
    
    @Test
    @DisplayName("getRefStepsArray should return empty array when both references are null or invalid")
    void getRefStepsArray_BothInvalid() {
        InvolvedUsersData data = new InvolvedUsersData(
            null, 
            " \n ", // Whitespace
            Collections.emptyList()
        );
        
        String[] result = data.getRefStepsArray();
        
        assertThat(result).isEmpty();
    }
}