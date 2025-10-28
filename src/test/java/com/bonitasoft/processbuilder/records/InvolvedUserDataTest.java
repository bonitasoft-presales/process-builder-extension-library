package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Robust test class for the InvolvedUsersData record, focusing on data integrity and immutability
 * of the membership list.
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
}