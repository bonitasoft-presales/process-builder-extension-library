package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import org.bonitasoft.engine.api.APIAccessor;
import org.bonitasoft.engine.api.IdentityAPI;
import org.bonitasoft.engine.api.ProfileAPI;
import org.bonitasoft.engine.profile.Profile;
import org.bonitasoft.engine.profile.ProfileMember;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Property-based tests for {@link ProfileUtis} utility class.
 * Tests invariants that must hold for any valid input.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("ProfileUtis Property-Based Tests")
class ProfileUtisPropertyTest {

    // =========================================================================
    // Input Validation Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should throw for null APIAccessor")
    void getUserIdsInProfile_shouldThrowForNullApiAccessor(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName) {

        assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(null, profileName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("APIAccessor cannot be null");
    }

    @Property(tries = 50)
    @Label("getUserIdsInProfile should throw for null profile name")
    void getUserIdsInProfile_shouldThrowForNullProfileName() {
        APIAccessor mockAccessor = mock(APIAccessor.class);

        assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(mockAccessor, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
    }

    @Property(tries = 50)
    @Label("getUserIdsInProfile should throw for empty profile name")
    void getUserIdsInProfile_shouldThrowForEmptyProfileName() {
        APIAccessor mockAccessor = mock(APIAccessor.class);

        assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(mockAccessor, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
    }

    @Property(tries = 50)
    @Label("getUserIdsInProfile should throw for blank profile name")
    void getUserIdsInProfile_shouldThrowForBlankProfileName(
            @ForAll("blankStrings") String blankName) {

        APIAccessor mockAccessor = mock(APIAccessor.class);

        assertThatThrownBy(() -> ProfileUtis.getUserIdsInProfile(mockAccessor, blankName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Profile name cannot be null or blank");
    }

    @Provide
    Arbitrary<String> blankStrings() {
        return Arbitraries.of("   ", "\t", "\n", "  \t\n  ", "    ");
    }

    // =========================================================================
    // Profile Not Found Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should return empty list for non-existent profile")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldReturnEmptyListForNonExistentProfile(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        SearchResult<Profile> mockSearchResult = mock(SearchResult.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockSearchResult.getResult()).thenReturn(List.of());
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockSearchResult);

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // Exception Handling Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should return empty list when search throws exception")
    void getUserIdsInProfile_shouldReturnEmptyListWhenSearchThrowsException(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class)))
                .thenThrow(new RuntimeException("Search failed"));

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // Profile Found with Empty Members Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should return empty list when profile has no members")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldReturnEmptyListWhenProfileHasNoMembers(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName,
            @ForAll @LongRange(min = 1, max = 10000) long profileId) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        Profile mockProfile = mock(Profile.class);
        SearchResult<Profile> mockProfileResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockMemberResult = mock(SearchResult.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfile.getId()).thenReturn(profileId);
        when(mockProfileResult.getResult()).thenReturn(List.of(mockProfile));
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockProfileResult);
        when(mockMemberResult.getResult()).thenReturn(List.of());
        when(mockProfileAPI.searchProfileMembers(anyString(), any(SearchOptions.class)))
                .thenReturn(mockMemberResult);

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result).isEmpty();
    }

    // =========================================================================
    // Direct User Collection Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should collect direct users when present")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldCollectDirectUsers(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName,
            @ForAll @LongRange(min = 1, max = 10000) long profileId,
            @ForAll @LongRange(min = 1, max = 10000) long userId) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        Profile mockProfile = mock(Profile.class);
        ProfileMember mockMember = mock(ProfileMember.class);
        SearchResult<Profile> mockProfileResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockUserMemberResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockEmptyResult = mock(SearchResult.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfile.getId()).thenReturn(profileId);
        when(mockProfileResult.getResult()).thenReturn(List.of(mockProfile));
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockProfileResult);

        // Setup direct user member
        when(mockMember.getUserId()).thenReturn(userId);
        when(mockUserMemberResult.getResult()).thenReturn(List.of(mockMember));
        when(mockEmptyResult.getResult()).thenReturn(List.of());

        when(mockProfileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(mockUserMemberResult);
        when(mockProfileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result).contains(userId);
    }

    // =========================================================================
    // User ID Filtering Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should filter out invalid user IDs (0 or negative)")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldFilterOutInvalidUserIds(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName,
            @ForAll @LongRange(min = 1, max = 10000) long profileId,
            @ForAll @LongRange(min = 1, max = 10000) long validUserId,
            @ForAll @LongRange(min = Long.MIN_VALUE, max = 0) long invalidUserId) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        Profile mockProfile = mock(Profile.class);
        ProfileMember mockValidMember = mock(ProfileMember.class);
        ProfileMember mockInvalidMember = mock(ProfileMember.class);
        SearchResult<Profile> mockProfileResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockUserMemberResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockEmptyResult = mock(SearchResult.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfile.getId()).thenReturn(profileId);
        when(mockProfileResult.getResult()).thenReturn(List.of(mockProfile));
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockProfileResult);

        // Setup members - one valid, one invalid
        when(mockValidMember.getUserId()).thenReturn(validUserId);
        when(mockInvalidMember.getUserId()).thenReturn(invalidUserId);
        when(mockUserMemberResult.getResult()).thenReturn(List.of(mockValidMember, mockInvalidMember));
        when(mockEmptyResult.getResult()).thenReturn(List.of());

        when(mockProfileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(mockUserMemberResult);
        when(mockProfileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result).contains(validUserId);
        assertThat(result).doesNotContain(invalidUserId);
    }

    // =========================================================================
    // Result Uniqueness Properties
    // =========================================================================

    @Property(tries = 50)
    @Label("getUserIdsInProfile should return unique user IDs (no duplicates)")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldReturnUniqueUserIds(
            @ForAll @AlphaChars @StringLength(min = 1, max = 30) String profileName,
            @ForAll @LongRange(min = 1, max = 10000) long profileId,
            @ForAll @LongRange(min = 1, max = 10000) long userId) throws Exception {

        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        Profile mockProfile = mock(Profile.class);
        ProfileMember mockMember1 = mock(ProfileMember.class);
        ProfileMember mockMember2 = mock(ProfileMember.class);
        SearchResult<Profile> mockProfileResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockUserMemberResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockEmptyResult = mock(SearchResult.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfile.getId()).thenReturn(profileId);
        when(mockProfileResult.getResult()).thenReturn(List.of(mockProfile));
        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockProfileResult);

        // Setup two members with same userId (duplicates)
        when(mockMember1.getUserId()).thenReturn(userId);
        when(mockMember2.getUserId()).thenReturn(userId);
        when(mockUserMemberResult.getResult()).thenReturn(List.of(mockMember1, mockMember2));
        when(mockEmptyResult.getResult()).thenReturn(List.of());

        when(mockProfileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(mockUserMemberResult);
        when(mockProfileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);

        List<Long> result = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        // Should only contain one instance of userId
        assertThat(result).containsExactly(userId);
    }

    // =========================================================================
    // Consistency Properties
    // =========================================================================

    @Property(tries = 30)
    @Label("getUserIdsInProfile should be consistent for same inputs")
    @SuppressWarnings("unchecked")
    void getUserIdsInProfile_shouldBeConsistentForSameInputs(
            @ForAll @AlphaChars @StringLength(min = 1, max = 20) String profileName,
            @ForAll @LongRange(min = 1, max = 1000) long profileId,
            @ForAll @LongRange(min = 1, max = 1000) long userId) throws Exception {

        // Create mocks that return the same data each time
        APIAccessor mockAccessor = mock(APIAccessor.class);
        ProfileAPI mockProfileAPI = mock(ProfileAPI.class);
        IdentityAPI mockIdentityAPI = mock(IdentityAPI.class);
        Profile mockProfile = mock(Profile.class);
        ProfileMember mockMember = mock(ProfileMember.class);

        when(mockAccessor.getProfileAPI()).thenReturn(mockProfileAPI);
        when(mockAccessor.getIdentityAPI()).thenReturn(mockIdentityAPI);
        when(mockProfile.getId()).thenReturn(profileId);
        when(mockMember.getUserId()).thenReturn(userId);

        SearchResult<Profile> mockProfileResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockUserMemberResult = mock(SearchResult.class);
        SearchResult<ProfileMember> mockEmptyResult = mock(SearchResult.class);

        when(mockProfileResult.getResult()).thenReturn(List.of(mockProfile));
        when(mockUserMemberResult.getResult()).thenReturn(List.of(mockMember));
        when(mockEmptyResult.getResult()).thenReturn(List.of());

        when(mockProfileAPI.searchProfiles(any(SearchOptions.class))).thenReturn(mockProfileResult);
        when(mockProfileAPI.searchProfileMembers(eq("USER"), any(SearchOptions.class)))
                .thenReturn(mockUserMemberResult);
        when(mockProfileAPI.searchProfileMembers(eq("ROLE"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("GROUP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);
        when(mockProfileAPI.searchProfileMembers(eq("MEMBERSHIP"), any(SearchOptions.class)))
                .thenReturn(mockEmptyResult);

        List<Long> result1 = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);
        List<Long> result2 = ProfileUtis.getUserIdsInProfile(mockAccessor, profileName);

        assertThat(result1).containsExactlyInAnyOrderElementsOf(result2);
    }
}
