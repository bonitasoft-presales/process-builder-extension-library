package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Robust test class for the InvolvedUsersData record, focusing on data integrity and immutability.
 */
class InvolvedUsersDataTest {

    private static final String MANAGER_REF = "Step_Start_Manager";
    private static final String USER_REF = "Step_Review_User";
    private List<String> mutableMemberships;
    private List<UserList> mutableUserList;
    private InvolvedUsersData involvedUsersData;

    @BeforeEach
    void setUp() {
        // Initialize mutable lists before each test
        mutableMemberships = new ArrayList<>(List.of("Role_HR", "Group_Dev"));
        
        // Assuming UserList is a record with Long persistenceId, etc.
        UserList user1 = new UserList(100L, true, "ref1", "KEY", 1L, 2L, 3L, 4L, 1);
        UserList user2 = new UserList(101L, false, "ref2", "KEY", null, 5L, null, 4L, 2);
        mutableUserList = new ArrayList<>(List.of(user1, user2));
        
        involvedUsersData = new InvolvedUsersData(
                MANAGER_REF, 
                USER_REF, 
                mutableMemberships, 
                mutableUserList
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
    void shouldContainAllListElements() {
        // Assert list contents are correct
        assertThat(involvedUsersData.memberships()).containsExactlyInAnyOrderElementsOf(mutableMemberships);
        assertThat(involvedUsersData.userList()).hasSize(mutableUserList.size());
    }

    // ------------------- Immutability (Defensive Copy) Tests -------------------

    @Test
    void constructorShouldCreateDefensiveCopiesOfLists() {
        // WHEN the external mutable lists are changed AFTER construction
        mutableMemberships.add("Role_Marketing");
        mutableUserList.clear(); // Clear the external list

        // THEN the internal state of the record must remain unchanged
        assertThat(involvedUsersData.memberships()).hasSize(2)
                .containsExactly("Role_HR", "Group_Dev");
        
        assertThat(involvedUsersData.userList()).hasSize(2);
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
    void userListAccessorShouldReturnUnmodifiableList() {
        List<UserList> returnedList = involvedUsersData.userList();
        
        // 1. Assert list is unmodifiable
        assertThatThrownBy(() -> returnedList.add(
                new UserList(999L, true, "test", "TEST", null, null, null, null, 99)))
            .isInstanceOf(UnsupportedOperationException.class);
            
        // 2. Assert that modification attempt does not affect the internal state
        assertThat(involvedUsersData.userList()).hasSize(2);
    }

    @Test
    void shouldHandleNullOrEmptyLists() {
        // Test case with null lists (should handle gracefully if the constructor allows it, 
        // though typically validation should prevent nulls)
        InvolvedUsersData emptyData = new InvolvedUsersData(
                "NoRef", 
                "NoRef", 
                Collections.emptyList(), 
                Collections.emptyList()
        );
        
        // Assertions for the empty case
        assertThat(emptyData.memberships()).isNotNull().isEmpty();
        assertThat(emptyData.userList()).isNotNull().isEmpty();
        
        // Attempt to modify the empty list returned by the accessor (still throws exception)
        assertThatThrownBy(() -> emptyData.memberships().add("Test"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}