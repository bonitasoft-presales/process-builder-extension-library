package com.bonitasoft.processbuilder.extension;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserCriterion;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileMember;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive test class for {@link ProfileUtis} utility class.
 * Ensures 100% code coverage including all methods, branches, and edge cases.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ProfileUtis Utility Tests")
class ProfileUtisTest {

    // =========================================================================
    // CONSTANTS
    // =========================================================================

    private static final String TEST_PROFILE_NAME = "Administrator";
    private static final long TEST_PROFILE_ID = 1L;
    private static final long TEST_USER_ID_1 = 100L;
    private static final long TEST_USER_ID_2 = 200L;
    private static final long TEST_USER_ID_3 = 300L;
    private static final long TEST_ROLE_ID = 10L;
    private static final long TEST_GROUP_ID = 20L;

    // =========================================================================
    // MOCKS
    // =========================================================================

    @Mock
    private APIAccessor apiAccessor;

    @Mock
    private ProfileAPI profileAPI;

    @Mock
    private IdentityAPI identityAPI;

    @Mock
    private Profile profile;

    @Mock
    private ProfileMember profileMemberUser;

    @Mock
    private ProfileMember profileMemberRole;

    @Mock
    private ProfileMember profileMemberGroup;

    @Mock
    private ProfileMember profileMemberMembership;

    @Mock
    private User user1;

    @Mock
    private User user2;

    @Mock
    private User user3;

    @Mock
    private SearchResult<Profile> profileSearchResult;

    @Mock
    private SearchResult<ProfileMember> memberSearchResult;

    @Mock
    private SearchResult<User> userSearchResult;

    // =========================================================================
    // SETUP
    // =========================================================================

    @BeforeEach
    void setUp() {
        when(apiAccessor.getProfileAPI()).thenReturn(profileAPI);
        when(apiAccessor.getIdentityAPI()).thenReturn(identityAPI);
        when(profile.getId()).thenReturn(TEST_PROFILE_ID);
        when(user1.getId()).thenReturn(TEST_USER_ID_1);
        when(user2.getId()).thenReturn(TEST_USER_ID_2);
        when(user3.getId()).thenReturn(TEST_USER_ID_3);
    }

    // =========================================================================
    // CONSTRUCTOR TESTS
    // =========================================================================

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Private constructor should throw UnsupportedOperationException")
        void constructor_should_throw_unsupported_operation_exception() throws Exception {
            Constructor<ProfileUtis> constructor = ProfileUtis.class.getDeclaredConstructor();

            assertThat(Modifier.isPrivate(constructor.getModifiers())).isTrue();

            constructor.setAccessible(true);

            InvocationTargetException exception = catchThrowableOfType(
                () -> constructor.newInstance(),
                InvocationTargetException.class
            );

            assertThat(exception.getCause())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("Utility class cannot be instantiated");
        }
    }

    // =========================================================================
    // INPUT VALIDATION TESTS
    // =========================================================================

    @Nested
    @DisplayName("Input Validation Tests")
    class InputValidationTests {

        @Test
        @DisplayName("should throw IllegalArgumentException when apiAccessor is null")
        void should_throw_when_api_accessor_is_null() {
            assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(null, TEST_PROFILE_NAME))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("APIAccessor cannot be null");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when profileName is null")
        void should_throw_when_profile_name_is_null() {
            assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(apiAccessor, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when profileName is empty")
        void should_throw_when_profile_name_is_empty() {
            assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(apiAccessor, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when profileName is blank")
        void should_throw_when_profile_name_is_blank() {
            assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(apiAccessor, "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
        }
    }

    // =========================================================================
    // PROFILE NOT FOUND TESTS
    // =========================================================================

    @Nested
    @DisplayName("Profile Not Found Tests")
    class ProfileNotFoundTests {

        @Test
        @DisplayName("should return empty list when profile is not found")
        void should_return_empty_list_when_profile_not_found() throws Exception {
            when(profileSearchResult.getResult()).thenReturn(List.of());
            when(profileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(profileSearchResult);

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, "NonExistentProfile");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should return empty list when search throws exception")
        void should_return_empty_list_when_search_throws_exception() throws Exception {
            when(profileAPI.searchProfiles(any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Search failed"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // DIRECT USERS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Direct Users Collection Tests")
    class DirectUsersTests {

        @Test
        @DisplayName("should collect direct users from profile")
        void should_collect_direct_users() throws Exception {
            setupProfileFound();
            setupDirectUsersMember();
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_1);
        }

        @Test
        @DisplayName("should filter out users with invalid ID (0 or negative)")
        void should_filter_invalid_user_ids() throws Exception {
            ProfileMember invalidMember = mock(ProfileMember.class);
            when(invalidMember.getUserId()).thenReturn(0L);

            setupProfileFound();
            when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberUser, invalidMember));
            when(profileMemberUser.getUserId()).thenReturn(TEST_USER_ID_1);
            when(profileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(memberSearchResult);
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).containsExactly(TEST_USER_ID_1);
            assertThat(result).doesNotContain(0L);
        }
    }

    // =========================================================================
    // ROLE USERS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Role Users Collection Tests")
    class RoleUsersTests {

        @Test
        @DisplayName("should collect users from roles")
        void should_collect_users_from_roles() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupRoleMember();
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            when(identityAPI.getActiveUsersInRole(eq(TEST_ROLE_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user2));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_2);
        }

        @Test
        @DisplayName("should filter out roles with invalid ID")
        void should_filter_invalid_role_ids() throws Exception {
            ProfileMember invalidRoleMember = mock(ProfileMember.class);
            when(invalidRoleMember.getRoleId()).thenReturn(0L);

            setupProfileFound();
            setupEmptyDirectUsers();

            when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberRole, invalidRoleMember));
            when(profileMemberRole.getRoleId()).thenReturn(TEST_ROLE_ID);
            when(profileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(memberSearchResult);

            when(identityAPI.getActiveUsersInRole(eq(TEST_ROLE_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user2));

            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_2);
            verify(identityAPI, times(1)).getActiveUsersInRole(anyLong(), eq(0), anyInt(), any(UserCriterion.class));
        }

        @Test
        @DisplayName("should handle exception when getting users in role")
        void should_handle_exception_getting_users_in_role() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupRoleMember();
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            when(identityAPI.getActiveUsersInRole(eq(TEST_ROLE_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenThrow(new RuntimeException("Role lookup failed"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // GROUP USERS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Group Users Collection Tests")
    class GroupUsersTests {

        @Test
        @DisplayName("should collect users from groups")
        void should_collect_users_from_groups() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupGroupMember();
            setupEmptyMembershipMembers();

            when(identityAPI.getActiveUsersInGroup(eq(TEST_GROUP_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user3));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_3);
        }

        @Test
        @DisplayName("should filter out groups with invalid ID")
        void should_filter_invalid_group_ids() throws Exception {
            ProfileMember invalidGroupMember = mock(ProfileMember.class);
            when(invalidGroupMember.getGroupId()).thenReturn(-1L);

            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();

            when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberGroup, invalidGroupMember));
            when(profileMemberGroup.getGroupId()).thenReturn(TEST_GROUP_ID);
            when(profileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(memberSearchResult);

            when(identityAPI.getActiveUsersInGroup(eq(TEST_GROUP_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user3));

            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_3);
        }

        @Test
        @DisplayName("should handle exception when getting users in group")
        void should_handle_exception_getting_users_in_group() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupGroupMember();
            setupEmptyMembershipMembers();

            when(identityAPI.getActiveUsersInGroup(eq(TEST_GROUP_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenThrow(new RuntimeException("Group lookup failed"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // MEMBERSHIP (ROLE + GROUP) TESTS
    // =========================================================================

    @Nested
    @DisplayName("Membership (Role + Group) Collection Tests")
    class MembershipTests {

        @Test
        @DisplayName("should collect users from role-group memberships")
        void should_collect_users_from_memberships() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            setupMembershipMember();

            when(userSearchResult.getResult()).thenReturn(List.of(user1));
            when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(userSearchResult);

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_1);
        }

        @Test
        @DisplayName("should filter memberships with invalid group or role ID")
        void should_filter_memberships_with_invalid_ids() throws Exception {
            ProfileMember invalidMembership = mock(ProfileMember.class);
            when(invalidMembership.getGroupId()).thenReturn(0L);
            when(invalidMembership.getRoleId()).thenReturn(TEST_ROLE_ID);

            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();

            when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberMembership, invalidMembership));
            when(profileMemberMembership.getGroupId()).thenReturn(TEST_GROUP_ID);
            when(profileMemberMembership.getRoleId()).thenReturn(TEST_ROLE_ID);
            when(profileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(memberSearchResult);

            when(userSearchResult.getResult()).thenReturn(List.of(user1));
            when(identityAPI.searchUsers(any(SearchOptions.class))).thenReturn(userSearchResult);

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).contains(TEST_USER_ID_1);
            verify(identityAPI, times(1)).searchUsers(any(SearchOptions.class));
        }

        @Test
        @DisplayName("should handle exception when searching users by group and role")
        void should_handle_exception_searching_users() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            setupMembershipMember();

            when(identityAPI.searchUsers(any(SearchOptions.class)))
                .thenThrow(new RuntimeException("User search failed"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // COMBINED USERS TESTS
    // =========================================================================

    @Nested
    @DisplayName("Combined Users Collection Tests")
    class CombinedUsersTests {

        @Test
        @DisplayName("should collect users from all sources and remove duplicates")
        void should_collect_from_all_sources_and_deduplicate() throws Exception {
            setupProfileFound();

            // Direct user: user1
            when(profileMemberUser.getUserId()).thenReturn(TEST_USER_ID_1);
            SearchResult<ProfileMember> directMemberResult = mock(SearchResult.class);
            when(directMemberResult.getResult()).thenReturn(List.of(profileMemberUser));
            when(profileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(directMemberResult);

            // Role member: user1 (duplicate), user2
            when(profileMemberRole.getRoleId()).thenReturn(TEST_ROLE_ID);
            SearchResult<ProfileMember> roleMemberResult = mock(SearchResult.class);
            when(roleMemberResult.getResult()).thenReturn(List.of(profileMemberRole));
            when(profileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(roleMemberResult);
            when(identityAPI.getActiveUsersInRole(eq(TEST_ROLE_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user1, user2)); // user1 is duplicate

            // Group member: user3
            when(profileMemberGroup.getGroupId()).thenReturn(TEST_GROUP_ID);
            SearchResult<ProfileMember> groupMemberResult = mock(SearchResult.class);
            when(groupMemberResult.getResult()).thenReturn(List.of(profileMemberGroup));
            when(profileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(groupMemberResult);
            when(identityAPI.getActiveUsersInGroup(eq(TEST_GROUP_ID), eq(0), anyInt(), any(UserCriterion.class)))
                .thenReturn(List.of(user3));

            // No membership members
            SearchResult<ProfileMember> membershipResult = mock(SearchResult.class);
            when(membershipResult.getResult()).thenReturn(List.of());
            when(profileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(membershipResult);

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            // Should have 3 unique users (user1 deduplicated)
            assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(TEST_USER_ID_1, TEST_USER_ID_2, TEST_USER_ID_3);
        }
    }

    // =========================================================================
    // EXCEPTION HANDLING TESTS
    // =========================================================================

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("should return empty list when unexpected exception occurs")
        void should_return_empty_list_on_unexpected_exception() throws Exception {
            when(profileSearchResult.getResult()).thenReturn(List.of(profile));
            when(profileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(profileSearchResult);
            when(profileAPI.searchProfileMembers(anyString(), any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle exception in collectDirectUsers")
        void should_handle_exception_in_collect_direct_users() throws Exception {
            setupProfileFound();
            when(profileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Direct users search failed"));
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle exception in collectUsersFromRoles")
        void should_handle_exception_in_collect_users_from_roles() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            when(profileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Roles search failed"));
            setupEmptyGroupMembers();
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle exception in collectUsersFromGroups")
        void should_handle_exception_in_collect_users_from_groups() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            when(profileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Groups search failed"));
            setupEmptyMembershipMembers();

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should handle exception in collectUsersFromMemberships")
        void should_handle_exception_in_collect_users_from_memberships() throws Exception {
            setupProfileFound();
            setupEmptyDirectUsers();
            setupEmptyRoleMembers();
            setupEmptyGroupMembers();
            when(profileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Memberships search failed"));

            List<Long> result = ProfileUtis.getUserIdsInProfile(apiAccessor, TEST_PROFILE_NAME);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void setupProfileFound() throws Exception {
        when(profileSearchResult.getResult()).thenReturn(List.of(profile));
        when(profileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(profileSearchResult);
    }

    private void setupDirectUsersMember() throws Exception {
        when(profileMemberUser.getUserId()).thenReturn(TEST_USER_ID_1);
        when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberUser));
        when(profileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
            .thenReturn(memberSearchResult);
    }

    private void setupEmptyDirectUsers() throws Exception {
        SearchResult<ProfileMember> emptyResult = mock(SearchResult.class);
        when(emptyResult.getResult()).thenReturn(List.of());
        when(profileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
            .thenReturn(emptyResult);
    }

    private void setupRoleMember() throws Exception {
        when(profileMemberRole.getRoleId()).thenReturn(TEST_ROLE_ID);
        when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberRole));
        when(profileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
            .thenReturn(memberSearchResult);
    }

    private void setupEmptyRoleMembers() throws Exception {
        SearchResult<ProfileMember> emptyResult = mock(SearchResult.class);
        when(emptyResult.getResult()).thenReturn(List.of());
        when(profileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
            .thenReturn(emptyResult);
    }

    private void setupGroupMember() throws Exception {
        when(profileMemberGroup.getGroupId()).thenReturn(TEST_GROUP_ID);
        when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberGroup));
        when(profileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
            .thenReturn(memberSearchResult);
    }

    private void setupEmptyGroupMembers() throws Exception {
        SearchResult<ProfileMember> emptyResult = mock(SearchResult.class);
        when(emptyResult.getResult()).thenReturn(List.of());
        when(profileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
            .thenReturn(emptyResult);
    }

    private void setupMembershipMember() throws Exception {
        when(profileMemberMembership.getGroupId()).thenReturn(TEST_GROUP_ID);
        when(profileMemberMembership.getRoleId()).thenReturn(TEST_ROLE_ID);
        when(memberSearchResult.getResult()).thenReturn(List.of(profileMemberMembership));
        when(profileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
            .thenReturn(memberSearchResult);
    }

    private void setupEmptyMembershipMembers() throws Exception {
        SearchResult<ProfileMember> emptyResult = mock(SearchResult.class);
        when(emptyResult.getResult()).thenReturn(List.of());
        when(profileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
            .thenReturn(emptyResult);
    }
}
