package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the {@link UserList} record.
 * This class verifies the correct behavior of the record's auto-generated
 * constructor, accessors, equality (hashCode/equals), and toString method.
 */
class UserListTest {

    // Common test data
    private static final Long PERSISTENCE_ID = 101L;
    private static final Boolean CAN_LAUNCH = true;
    private static final String REF_MEMBERSHIP = "Process Managers";
    private static final String MEMBERSHIP_KEY = "ROLE";
    private static final Long GROUP_ID = 50L;
    private static final Long ROLE_ID = 10L;
    private static final Long USER_ID = null; // Set to null for this specific entry
    private static final Long PB_PROCESS_ID = 2000L;

    /**
     * Test case to verify the correct construction and accessor methods.
     */
    @Test
    @DisplayName("Should correctly instantiate UserList and expose all fields via accessors")
    void should_InstantiateAndExposeFields_Correctly() {
        // Given
        UserList userList = new UserList(
            PERSISTENCE_ID,
            CAN_LAUNCH,
            REF_MEMBERSHIP,
            MEMBERSHIP_KEY,
            GROUP_ID,
            ROLE_ID,
            USER_ID,
            PB_PROCESS_ID
        );

        // Then
        assertThat(userList).isNotNull();
        assertThat(userList.persistenceId()).isEqualTo(PERSISTENCE_ID);
        assertThat(userList.canLaunchProcess()).isEqualTo(CAN_LAUNCH);
        assertThat(userList.refMemberShip()).isEqualTo(REF_MEMBERSHIP);
        assertThat(userList.memberShipKey()).isEqualTo(MEMBERSHIP_KEY);
        assertThat(userList.groupId()).isEqualTo(GROUP_ID);
        assertThat(userList.roleId()).isEqualTo(ROLE_ID);
        assertThat(userList.userId()).isNull(); // Check for the expected null value
        assertThat(userList.pBProcessPersistenceId()).isEqualTo(PB_PROCESS_ID);
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
        UserList userList1 = new UserList(
            PERSISTENCE_ID, CAN_LAUNCH, REF_MEMBERSHIP, MEMBERSHIP_KEY, GROUP_ID, ROLE_ID, USER_ID, PB_PROCESS_ID
        );
        UserList userList2 = new UserList(
            PERSISTENCE_ID, CAN_LAUNCH, REF_MEMBERSHIP, MEMBERSHIP_KEY, GROUP_ID, ROLE_ID, USER_ID, PB_PROCESS_ID
        );

        // A different instance (only changing the ID)
        UserList differentUserList = new UserList(
            999L, CAN_LAUNCH, REF_MEMBERSHIP, MEMBERSHIP_KEY, GROUP_ID, ROLE_ID, USER_ID, PB_PROCESS_ID
        );

        // Then
        // 1. Instances with the same field values should be equal and have the same hash code
        assertThat(userList1)
            .isEqualTo(userList2)
            .hasSameHashCodeAs(userList2);

        // 2. An instance should not be equal to a different instance
        assertThat(userList1).isNotEqualTo(differentUserList);
        
        // 3. Hash codes of different instances are usually different (though not guaranteed)
        assertThat(userList1.hashCode()).isNotEqualTo(differentUserList.hashCode());
        
        // 4. Record should not be equal to null or another class
        assertThat(userList1).isNotEqualTo(null);
        assertThat(userList1).isNotEqualTo(new Object());
    }

    //-------------------------------------------------------------------------

    /**
     * Test case to verify the generated toString() method, which is useful for logging.
     */
    @Test
    @DisplayName("Should generate a useful toString representation")
    void should_Generate_ToString() {
        // Given
        UserList userList = new UserList(
            PERSISTENCE_ID, CAN_LAUNCH, REF_MEMBERSHIP, MEMBERSHIP_KEY, GROUP_ID, ROLE_ID, USER_ID, PB_PROCESS_ID
        );
        
        // When
        String result = userList.toString();

        // Then
        // Records typically format toString as: ClassName[field1=value1, field2=value2, ...]
        assertThat(result)
            .contains("UserList[")
            .contains("persistenceId=" + PERSISTENCE_ID)
            .contains("refMemberShip=" + REF_MEMBERSHIP)
            .contains("groupId=" + GROUP_ID)
            .contains("userId=" + USER_ID) // Null should be included
            .endsWith("]");
    }
}