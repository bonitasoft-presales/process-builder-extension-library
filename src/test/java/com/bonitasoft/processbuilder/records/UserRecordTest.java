package com.bonitasoft.processbuilder.records;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UserRecordTest {

    private static final Long ID = 101L;
    private static final String USER_NAME = "walter.bates";
    private static final String FULL_NAME = "Walter Bates";
    private static final String FIRST_NAME = "Walter";
    private static final String LAST_NAME = "Bates";
    private static final String EMAIL = "walter.bates@example.com";

    private UserRecord createDefaultUser() {
        return new UserRecord(ID, USER_NAME, FULL_NAME, FIRST_NAME, LAST_NAME, EMAIL);
    }

    @Test
    @DisplayName("Should correctly instantiate User and expose all fields via accessors")
    void should_InstantiateAndExposeFields_Correctly() {
        // Given
        UserRecord user = createDefaultUser();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.id()).isEqualTo(ID);
        assertThat(user.userName()).isEqualTo(USER_NAME);
        assertThat(user.fullName()).isEqualTo(FULL_NAME);
        assertThat(user.firstName()).isEqualTo(FIRST_NAME);
        assertThat(user.lastName()).isEqualTo(LAST_NAME);
        assertThat(user.email()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should correctly implement equals and hashCode")
    void should_ImplementEqualsAndHashCode_Correctly() {
        // Given
        UserRecord user1 = createDefaultUser();
        UserRecord user2 = createDefaultUser();
        UserRecord differentUser = new UserRecord(999L, USER_NAME, FULL_NAME, FIRST_NAME, LAST_NAME, EMAIL);

        // Then
        assertThat(user1).isEqualTo(user2).hasSameHashCodeAs(user2);
        assertThat(user1).isNotEqualTo(differentUser);
    }

    @Test
    @DisplayName("Should generate a useful toString representation")
    void should_Generate_ToString() {
        // Given
        UserRecord user = createDefaultUser();

        // When
        String result = user.toString();

        // Then
        assertThat(result)
            .contains("UserRecord[")
            .contains("id=" + ID)
            .contains("userName=" + USER_NAME)
            .contains("email=" + EMAIL)
            .endsWith("]");
    }

    @Test
    @DisplayName("Should allow null ID for representing unknown users")
    void should_AllowNullId() {
        // Given & When
        UserRecord user = new UserRecord(null, USER_NAME, FULL_NAME, FIRST_NAME, LAST_NAME, EMAIL);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.id()).isNull();
        assertThat(user.userName()).isEqualTo(USER_NAME);
    }
}