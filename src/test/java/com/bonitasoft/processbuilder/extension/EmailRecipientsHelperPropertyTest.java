package com.bonitasoft.processbuilder.extension;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

/**
 * Property-based tests for {@link EmailRecipientsHelper} utility class.
 * Tests the DAO-independent utility methods that don't require Bonita API.
 *
 * @author Bonitasoft
 * @since 1.0
 */
@Label("EmailRecipientsHelper Property-Based Tests")
class EmailRecipientsHelperPropertyTest {

    // =========================================================================
    // isValidUserId PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isValidUserId should return true for positive longs")
    void isValidUserIdShouldReturnTrueForPositive(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long userId) {

        boolean result = EmailRecipientsHelper.isValidUserId(userId);

        assertThat(result).isTrue();
    }

    @Property(tries = 300)
    @Label("isValidUserId should return false for zero")
    void isValidUserIdShouldReturnFalseForZero() {
        boolean result = EmailRecipientsHelper.isValidUserId(0L);

        assertThat(result).isFalse();
    }

    @Property(tries = 300)
    @Label("isValidUserId should return false for negative longs")
    void isValidUserIdShouldReturnFalseForNegative(
            @ForAll @LongRange(min = Long.MIN_VALUE, max = -1) long userId) {

        boolean result = EmailRecipientsHelper.isValidUserId(userId);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isValidUserId should return false for null")
    void isValidUserIdShouldReturnFalseForNull() {
        boolean result = EmailRecipientsHelper.isValidUserId(null);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // isValidEmail PROPERTIES
    // =========================================================================

    @Property(tries = 500)
    @Label("isValidEmail should return true for non-blank strings")
    void isValidEmailShouldReturnTrueForNonBlank(
            @ForAll @StringLength(min = 1, max = 100) String email) {

        // Skip strings that are only whitespace
        Assume.that(!email.isBlank());

        boolean result = EmailRecipientsHelper.isValidEmail(email);

        assertThat(result).isTrue();
    }

    @Property(tries = 100)
    @Label("isValidEmail should return false for null")
    void isValidEmailShouldReturnFalseForNull() {
        boolean result = EmailRecipientsHelper.isValidEmail(null);

        assertThat(result).isFalse();
    }

    @Property(tries = 100)
    @Label("isValidEmail should return false for empty string")
    void isValidEmailShouldReturnFalseForEmpty() {
        boolean result = EmailRecipientsHelper.isValidEmail("");

        assertThat(result).isFalse();
    }

    @Property(tries = 200)
    @Label("isValidEmail should return false for blank strings")
    void isValidEmailShouldReturnFalseForBlank(
            @ForAll @IntRange(min = 1, max = 20) int spaces) {

        String blankEmail = " ".repeat(spaces);
        boolean result = EmailRecipientsHelper.isValidEmail(blankEmail);

        assertThat(result).isFalse();
    }

    // =========================================================================
    // filterValidEmails PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("filterValidEmails should filter out null and blank entries")
    void filterValidEmailsShouldFilterOutInvalid(
            @ForAll @Size(min = 0, max = 10) List<@StringLength(min = 0, max = 50) String> emails) {

        // Add some nulls to the list
        List<String> withNulls = new ArrayList<>(emails);
        withNulls.add(null);
        withNulls.add("");
        withNulls.add("   ");

        Set<String> result = EmailRecipientsHelper.filterValidEmails(withNulls);

        // Result should not contain nulls or blanks
        assertThat(result).noneMatch(e -> e == null || e.isBlank());
    }

    @Property(tries = 200)
    @Label("filterValidEmails should return empty set for null input")
    void filterValidEmailsShouldReturnEmptyForNull() {
        Set<String> result = EmailRecipientsHelper.filterValidEmails(null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 200)
    @Label("filterValidEmails should return empty set for empty input")
    void filterValidEmailsShouldReturnEmptyForEmpty() {
        Set<String> result = EmailRecipientsHelper.filterValidEmails(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("filterValidEmails should preserve valid emails")
    void filterValidEmailsShouldPreserveValid(
            @ForAll @Size(min = 1, max = 10) List<@StringLength(min = 1, max = 50) @AlphaChars String> emails) {

        Set<String> result = EmailRecipientsHelper.filterValidEmails(emails);

        // All input emails should be in result (as they are non-blank)
        assertThat(result).containsAll(emails.stream()
                .filter(e -> e != null && !e.isBlank())
                .toList());
    }

    // =========================================================================
    // joinEmails PROPERTIES
    // =========================================================================

    @Property(tries = 200)
    @Label("joinEmails should return empty string for null input")
    void joinEmailsShouldReturnEmptyForNull() {
        String result = EmailRecipientsHelper.joinEmails(null);

        assertThat(result).isEmpty();
    }

    @Property(tries = 200)
    @Label("joinEmails should return empty string for empty input")
    void joinEmailsShouldReturnEmptyForEmpty() {
        String result = EmailRecipientsHelper.joinEmails(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("joinEmails should join with comma and space separator")
    void joinEmailsShouldJoinWithCommaSeparator(
            @ForAll @Size(min = 2, max = 5) List<@StringLength(min = 1, max = 30) @AlphaChars String> emails) {

        String result = EmailRecipientsHelper.joinEmails(emails);

        // Result should contain comma-space separator if multiple emails
        if (emails.size() > 1) {
            assertThat(result).contains(", ");
        }
    }

    @Property(tries = 300)
    @Label("joinEmails should filter out invalid emails before joining")
    void joinEmailsShouldFilterInvalid() {
        List<String> emailsWithInvalid = Arrays.asList("valid1", null, "valid2", "", "   ", "valid3");

        String result = EmailRecipientsHelper.joinEmails(emailsWithInvalid);

        // Should not contain empty parts
        assertThat(result).doesNotContain(", ,");
        assertThat(result).doesNotContain(",  ");

        // Should contain valid emails
        assertThat(result).contains("valid1");
        assertThat(result).contains("valid2");
        assertThat(result).contains("valid3");
    }

    @Property(tries = 200)
    @Label("joinEmails should return single email without separator")
    void joinEmailsShouldReturnSingleWithoutSeparator(
            @ForAll @StringLength(min = 1, max = 50) @AlphaChars String email) {

        String result = EmailRecipientsHelper.joinEmails(Collections.singletonList(email));

        assertThat(result).isEqualTo(email);
        assertThat(result).doesNotContain(",");
    }

    // =========================================================================
    // extractUserIdFromFirstStep PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("extractUserIdFromFirstStep should return null for null list")
    void extractUserIdFromFirstStepShouldReturnNullForNullList() {
        Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(null, obj -> 1L);

        assertThat(result).isNull();
    }

    @Property(tries = 300)
    @Label("extractUserIdFromFirstStep should return null for empty list")
    void extractUserIdFromFirstStepShouldReturnNullForEmptyList() {
        Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(Collections.emptyList(), obj -> 1L);

        assertThat(result).isNull();
    }

    @Property(tries = 300)
    @Label("extractUserIdFromFirstStep should return extracted userId from first element")
    void extractUserIdFromFirstStepShouldExtractFromFirst(
            @ForAll @LongRange(min = 1, max = Long.MAX_VALUE) long expectedUserId,
            @ForAll @Size(min = 1, max = 10) List<@LongRange(min = 1, max = Long.MAX_VALUE) Long> otherIds) {

        // Create test objects with userId as their value
        List<Long> steps = new ArrayList<>();
        steps.add(expectedUserId);
        steps.addAll(otherIds);

        Function<Long, Long> extractor = id -> id;

        Long result = EmailRecipientsHelper.extractUserIdFromFirstStep(steps, extractor);

        assertThat(result).isEqualTo(expectedUserId);
    }

    @Property(tries = 100)
    @Label("extractUserIdFromFirstStep should throw NPE for null extractor")
    void extractUserIdFromFirstStepShouldThrowForNullExtractor() {
        List<String> steps = Collections.singletonList("test");

        assertThatThrownBy(() -> EmailRecipientsHelper.extractUserIdFromFirstStep(steps, null))
                .isInstanceOf(NullPointerException.class);
    }

    // =========================================================================
    // extractUserIdsFromMembershipResults PROPERTIES
    // =========================================================================

    @Property(tries = 300)
    @Label("extractUserIdsFromMembershipResults should return empty set for null")
    void extractUserIdsFromMembershipShouldReturnEmptyForNull() {
        Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(null, obj -> 1L);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("extractUserIdsFromMembershipResults should return empty set for empty collection")
    void extractUserIdsFromMembershipShouldReturnEmptyForEmpty() {
        Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(Collections.emptyList(), obj -> 1L);

        assertThat(result).isEmpty();
    }

    @Property(tries = 300)
    @Label("extractUserIdsFromMembershipResults should filter invalid userIds")
    void extractUserIdsFromMembershipShouldFilterInvalid() {
        List<Long> userLists = Arrays.asList(1L, 0L, -1L, 5L, null, 10L);

        Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(userLists, id -> id);

        // Should only contain valid (positive) IDs
        assertThat(result).containsExactlyInAnyOrder(1L, 5L, 10L);
        assertThat(result).doesNotContain(0L);
    }

    @Property(tries = 300)
    @Label("extractUserIdsFromMembershipResults should return unique IDs")
    void extractUserIdsFromMembershipShouldReturnUnique(
            @ForAll @LongRange(min = 1, max = 1000) long duplicateId) {

        List<Long> userLists = Arrays.asList(duplicateId, duplicateId, duplicateId);

        Set<Long> result = EmailRecipientsHelper.extractUserIdsFromMembershipResults(userLists, id -> id);

        assertThat(result).hasSize(1);
        assertThat(result).contains(duplicateId);
    }

    @Property(tries = 100)
    @Label("extractUserIdsFromMembershipResults should throw NPE for null extractor")
    void extractUserIdsFromMembershipShouldThrowForNullExtractor() {
        List<String> userLists = Collections.singletonList("test");

        assertThatThrownBy(() -> EmailRecipientsHelper.extractUserIdsFromMembershipResults(userLists, null))
                .isInstanceOf(NullPointerException.class);
    }

    // =========================================================================
    // getStepIdParameterKey PROPERTIES
    // =========================================================================

    @Property(tries = 100)
    @Label("getStepIdParameterKey should return consistent non-null value")
    void getStepIdParameterKeyShouldReturnConsistentValue() {
        String result1 = EmailRecipientsHelper.getStepIdParameterKey();
        String result2 = EmailRecipientsHelper.getStepIdParameterKey();

        assertThat(result1).isNotNull().isNotBlank();
        assertThat(result1).isEqualTo(result2);
        assertThat(result1).isEqualTo("recipients.stepId");
    }
}
