package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ThemeType} enumeration.
 * <p>
 * This class ensures that all defined constants are present and that
 * utility methods, such as {@code isValid}, {@code getAllData}, and
 * {@code getAllKeysList}, function correctly across various inputs.
 * </p>
 */
class ThemeTypeTest {

    private static final int EXPECTED_CONSTANT_COUNT = 20;

    // -------------------------------------------------------------------------
    // Constant Count Test
    // -------------------------------------------------------------------------

    /**
     * Tests that the enum contains the expected number of constants.
     */
    @Test
    @DisplayName("Should contain exactly 20 theme constants")
    void should_contain_expected_number_of_constants() {
        assertEquals(EXPECTED_CONSTANT_COUNT, ThemeType.values().length);
    }

    // -------------------------------------------------------------------------
    // Brand Identity Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the PRIMARY_COLOR constant existence and values.
     */
    @Test
    @DisplayName("PRIMARY_COLOR constant should be defined with correct key and description")
    void primaryColor_should_have_correct_values() {
        assertEquals("PRIMARY_COLOR", ThemeType.PRIMARY_COLOR.name());
        assertEquals("PrimaryColor", ThemeType.PRIMARY_COLOR.getKey());
        assertTrue(ThemeType.PRIMARY_COLOR.getDescription().contains("brand color"));
    }

    /**
     * Tests the SECONDARY_COLOR constant existence and values.
     */
    @Test
    @DisplayName("SECONDARY_COLOR constant should be defined with correct key and description")
    void secondaryColor_should_have_correct_values() {
        assertEquals("SECONDARY_COLOR", ThemeType.SECONDARY_COLOR.name());
        assertEquals("SecondaryColor", ThemeType.SECONDARY_COLOR.getKey());
        assertTrue(ThemeType.SECONDARY_COLOR.getDescription().contains("secondary"));
    }

    /**
     * Tests the LOGO_URL constant existence and values.
     */
    @Test
    @DisplayName("LOGO_URL constant should be defined with correct key and description")
    void logoUrl_should_have_correct_values() {
        assertEquals("LOGO_URL", ThemeType.LOGO_URL.name());
        assertEquals("LogoUrl", ThemeType.LOGO_URL.getKey());
        assertTrue(ThemeType.LOGO_URL.getDescription().contains("logo"));
    }

    /**
     * Tests the FAVICON_URL constant existence and values.
     */
    @Test
    @DisplayName("FAVICON_URL constant should be defined with correct key and description")
    void faviconUrl_should_have_correct_values() {
        assertEquals("FAVICON_URL", ThemeType.FAVICON_URL.name());
        assertEquals("FaviconUrl", ThemeType.FAVICON_URL.getKey());
        assertTrue(ThemeType.FAVICON_URL.getDescription().contains("favicon"));
    }

    /**
     * Tests the APP_NAME constant existence and values.
     */
    @Test
    @DisplayName("APP_NAME constant should be defined with correct key and description")
    void appName_should_have_correct_values() {
        assertEquals("APP_NAME", ThemeType.APP_NAME.name());
        assertEquals("AppName", ThemeType.APP_NAME.getKey());
        assertTrue(ThemeType.APP_NAME.getDescription().contains("application name"));
    }

    // -------------------------------------------------------------------------
    // Typography Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the FONT_FAMILY constant existence and values.
     */
    @Test
    @DisplayName("FONT_FAMILY constant should be defined with correct key and description")
    void fontFamily_should_have_correct_values() {
        assertEquals("FONT_FAMILY", ThemeType.FONT_FAMILY.name());
        assertEquals("FontFamily", ThemeType.FONT_FAMILY.getKey());
        assertTrue(ThemeType.FONT_FAMILY.getDescription().contains("font family"));
    }

    /**
     * Tests the HEADING_FONT_FAMILY constant existence and values.
     */
    @Test
    @DisplayName("HEADING_FONT_FAMILY constant should be defined with correct key and description")
    void headingFontFamily_should_have_correct_values() {
        assertEquals("HEADING_FONT_FAMILY", ThemeType.HEADING_FONT_FAMILY.name());
        assertEquals("HeadingFontFamily", ThemeType.HEADING_FONT_FAMILY.getKey());
        assertTrue(ThemeType.HEADING_FONT_FAMILY.getDescription().contains("headings"));
    }

    /**
     * Tests the BASE_FONT_SIZE constant existence and values.
     */
    @Test
    @DisplayName("BASE_FONT_SIZE constant should be defined with correct key and description")
    void baseFontSize_should_have_correct_values() {
        assertEquals("BASE_FONT_SIZE", ThemeType.BASE_FONT_SIZE.name());
        assertEquals("BaseFontSize", ThemeType.BASE_FONT_SIZE.getKey());
        assertTrue(ThemeType.BASE_FONT_SIZE.getDescription().contains("font size"));
    }

    // -------------------------------------------------------------------------
    // Background Colors Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the BACKGROUND_COLOR constant existence and values.
     */
    @Test
    @DisplayName("BACKGROUND_COLOR constant should be defined with correct key and description")
    void backgroundColor_should_have_correct_values() {
        assertEquals("BACKGROUND_COLOR", ThemeType.BACKGROUND_COLOR.name());
        assertEquals("BackgroundColor", ThemeType.BACKGROUND_COLOR.getKey());
        assertTrue(ThemeType.BACKGROUND_COLOR.getDescription().contains("background color"));
    }

    /**
     * Tests the HEADER_BACKGROUND constant existence and values.
     */
    @Test
    @DisplayName("HEADER_BACKGROUND constant should be defined with correct key and description")
    void headerBackground_should_have_correct_values() {
        assertEquals("HEADER_BACKGROUND", ThemeType.HEADER_BACKGROUND.name());
        assertEquals("HeaderBackground", ThemeType.HEADER_BACKGROUND.getKey());
        assertTrue(ThemeType.HEADER_BACKGROUND.getDescription().contains("header"));
    }

    /**
     * Tests the SIDEBAR_BACKGROUND constant existence and values.
     */
    @Test
    @DisplayName("SIDEBAR_BACKGROUND constant should be defined with correct key and description")
    void sidebarBackground_should_have_correct_values() {
        assertEquals("SIDEBAR_BACKGROUND", ThemeType.SIDEBAR_BACKGROUND.name());
        assertEquals("SidebarBackground", ThemeType.SIDEBAR_BACKGROUND.getKey());
        assertTrue(ThemeType.SIDEBAR_BACKGROUND.getDescription().contains("sidebar"));
    }

    /**
     * Tests the FOOTER_BACKGROUND constant existence and values.
     */
    @Test
    @DisplayName("FOOTER_BACKGROUND constant should be defined with correct key and description")
    void footerBackground_should_have_correct_values() {
        assertEquals("FOOTER_BACKGROUND", ThemeType.FOOTER_BACKGROUND.name());
        assertEquals("FooterBackground", ThemeType.FOOTER_BACKGROUND.getKey());
        assertTrue(ThemeType.FOOTER_BACKGROUND.getDescription().contains("footer"));
    }

    // -------------------------------------------------------------------------
    // Text Colors Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the TEXT_COLOR constant existence and values.
     */
    @Test
    @DisplayName("TEXT_COLOR constant should be defined with correct key and description")
    void textColor_should_have_correct_values() {
        assertEquals("TEXT_COLOR", ThemeType.TEXT_COLOR.name());
        assertEquals("TextColor", ThemeType.TEXT_COLOR.getKey());
        assertTrue(ThemeType.TEXT_COLOR.getDescription().contains("text color"));
    }

    /**
     * Tests the TEXT_ON_PRIMARY constant existence and values.
     */
    @Test
    @DisplayName("TEXT_ON_PRIMARY constant should be defined with correct key and description")
    void textOnPrimary_should_have_correct_values() {
        assertEquals("TEXT_ON_PRIMARY", ThemeType.TEXT_ON_PRIMARY.name());
        assertEquals("TextOnPrimary", ThemeType.TEXT_ON_PRIMARY.getKey());
        assertTrue(ThemeType.TEXT_ON_PRIMARY.getDescription().contains("primary color"));
    }

    /**
     * Tests the LINK_COLOR constant existence and values.
     */
    @Test
    @DisplayName("LINK_COLOR constant should be defined with correct key and description")
    void linkColor_should_have_correct_values() {
        assertEquals("LINK_COLOR", ThemeType.LINK_COLOR.name());
        assertEquals("LinkColor", ThemeType.LINK_COLOR.getKey());
        assertTrue(ThemeType.LINK_COLOR.getDescription().contains("hyperlinks"));
    }

    // -------------------------------------------------------------------------
    // Status Colors Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the SUCCESS_COLOR constant existence and values.
     */
    @Test
    @DisplayName("SUCCESS_COLOR constant should be defined with correct key and description")
    void successColor_should_have_correct_values() {
        assertEquals("SUCCESS_COLOR", ThemeType.SUCCESS_COLOR.name());
        assertEquals("SuccessColor", ThemeType.SUCCESS_COLOR.getKey());
        assertTrue(ThemeType.SUCCESS_COLOR.getDescription().contains("success"));
    }

    /**
     * Tests the WARNING_COLOR constant existence and values.
     */
    @Test
    @DisplayName("WARNING_COLOR constant should be defined with correct key and description")
    void warningColor_should_have_correct_values() {
        assertEquals("WARNING_COLOR", ThemeType.WARNING_COLOR.name());
        assertEquals("WarningColor", ThemeType.WARNING_COLOR.getKey());
        assertTrue(ThemeType.WARNING_COLOR.getDescription().contains("warning"));
    }

    /**
     * Tests the ERROR_COLOR constant existence and values.
     */
    @Test
    @DisplayName("ERROR_COLOR constant should be defined with correct key and description")
    void errorColor_should_have_correct_values() {
        assertEquals("ERROR_COLOR", ThemeType.ERROR_COLOR.name());
        assertEquals("ErrorColor", ThemeType.ERROR_COLOR.getKey());
        assertTrue(ThemeType.ERROR_COLOR.getDescription().contains("error"));
    }

    /**
     * Tests the INFO_COLOR constant existence and values.
     */
    @Test
    @DisplayName("INFO_COLOR constant should be defined with correct key and description")
    void infoColor_should_have_correct_values() {
        assertEquals("INFO_COLOR", ThemeType.INFO_COLOR.name());
        assertEquals("InfoColor", ThemeType.INFO_COLOR.getKey());
        assertTrue(ThemeType.INFO_COLOR.getDescription().contains("informational"));
    }

    // -------------------------------------------------------------------------
    // UI Elements Constants Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the BORDER_RADIUS constant existence and values.
     */
    @Test
    @DisplayName("BORDER_RADIUS constant should be defined with correct key and description")
    void borderRadius_should_have_correct_values() {
        assertEquals("BORDER_RADIUS", ThemeType.BORDER_RADIUS.name());
        assertEquals("BorderRadius", ThemeType.BORDER_RADIUS.getKey());
        assertTrue(ThemeType.BORDER_RADIUS.getDescription().contains("border radius"));
    }

    // -------------------------------------------------------------------------
    // isValid Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests the {@code isValid} method with a valid constant name (uppercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid uppercase name")
    void isValid_should_return_true_for_valid_uppercase() {
        assertTrue(ThemeType.isValid("PRIMARY_COLOR"));
        assertTrue(ThemeType.isValid("BORDER_RADIUS"));
    }

    /**
     * Tests the {@code isValid} method with a valid constant name (lowercase).
     */
    @Test
    @DisplayName("isValid should return true for a valid lowercase name")
    void isValid_should_return_true_for_valid_lowercase() {
        assertTrue(ThemeType.isValid("secondary_color"));
        assertTrue(ThemeType.isValid("error_color"));
    }

    /**
     * Tests the {@code isValid} method with a mixed-case constant name.
     */
    @Test
    @DisplayName("isValid should return true for a mixed-case name")
    void isValid_should_return_true_for_mixed_case() {
        assertTrue(ThemeType.isValid("Font_Family"));
        assertTrue(ThemeType.isValid("Header_Background"));
    }

    /**
     * Tests the {@code isValid} method with a non-existent name.
     */
    @Test
    @DisplayName("isValid should return false for an invalid name")
    void isValid_should_return_false_for_invalid_name() {
        assertFalse(ThemeType.isValid("NON_EXISTENT_TYPE"));
        assertFalse(ThemeType.isValid("INVALID_COLOR"));
    }

    /**
     * Tests the {@code isValid} method with a null input.
     */
    @Test
    @DisplayName("isValid should return false for null input")
    void isValid_should_return_false_for_null() {
        assertFalse(ThemeType.isValid(null));
    }

    /**
     * Tests the {@code isValid} method with an empty string input.
     */
    @Test
    @DisplayName("isValid should return false for an empty string input")
    void isValid_should_return_false_for_empty_string() {
        assertFalse(ThemeType.isValid(""));
    }

    /**
     * Tests the {@code isValid} method with whitespace-padded input.
     */
    @Test
    @DisplayName("isValid should return true for whitespace-padded valid name")
    void isValid_should_return_true_for_whitespace_padded_name() {
        assertTrue(ThemeType.isValid("  PRIMARY_COLOR  "));
        assertTrue(ThemeType.isValid("\tSUCCESS_COLOR\t"));
    }

    /**
     * Tests the {@code isValid} method with whitespace-only input.
     */
    @Test
    @DisplayName("isValid should return false for whitespace-only input")
    void isValid_should_return_false_for_whitespace_only() {
        assertFalse(ThemeType.isValid("   "));
        assertFalse(ThemeType.isValid("\t"));
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllData returns a map with all constants.
     */
    @Test
    @DisplayName("getAllData should return map with all theme constants")
    void getAllData_shouldReturnCorrectMap() {
        Map<String, String> data = ThemeType.getAllData();
        assertEquals(EXPECTED_CONSTANT_COUNT, data.size());

        // Verify brand identity keys
        assertTrue(data.containsKey("PrimaryColor"));
        assertTrue(data.containsKey("SecondaryColor"));
        assertTrue(data.containsKey("LogoUrl"));
        assertTrue(data.containsKey("FaviconUrl"));
        assertTrue(data.containsKey("AppName"));

        // Verify typography keys
        assertTrue(data.containsKey("FontFamily"));
        assertTrue(data.containsKey("HeadingFontFamily"));
        assertTrue(data.containsKey("BaseFontSize"));

        // Verify background keys
        assertTrue(data.containsKey("BackgroundColor"));
        assertTrue(data.containsKey("HeaderBackground"));
        assertTrue(data.containsKey("SidebarBackground"));
        assertTrue(data.containsKey("FooterBackground"));

        // Verify text color keys
        assertTrue(data.containsKey("TextColor"));
        assertTrue(data.containsKey("TextOnPrimary"));
        assertTrue(data.containsKey("LinkColor"));

        // Verify status color keys
        assertTrue(data.containsKey("SuccessColor"));
        assertTrue(data.containsKey("WarningColor"));
        assertTrue(data.containsKey("ErrorColor"));
        assertTrue(data.containsKey("InfoColor"));

        // Verify UI element keys
        assertTrue(data.containsKey("BorderRadius"));
    }

    /**
     * Tests that getAllData returns an unmodifiable map.
     */
    @Test
    @DisplayName("getAllData should return an unmodifiable map")
    void getAllData_shouldReturnUnmodifiableMap() {
        Map<String, String> data = ThemeType.getAllData();
        assertThrows(UnsupportedOperationException.class, () -> data.clear());
        assertThrows(UnsupportedOperationException.class, () -> data.put("Test", "Value"));
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    /**
     * Tests that getAllKeysList returns a list with all keys.
     */
    @Test
    @DisplayName("getAllKeysList should return list with all theme keys")
    void getAllKeysList_shouldReturnCorrectList() {
        List<String> keys = ThemeType.getAllKeysList();
        assertEquals(EXPECTED_CONSTANT_COUNT, keys.size());

        // Verify some representative keys from each category
        assertTrue(keys.contains("PrimaryColor"));
        assertTrue(keys.contains("FontFamily"));
        assertTrue(keys.contains("BackgroundColor"));
        assertTrue(keys.contains("TextColor"));
        assertTrue(keys.contains("SuccessColor"));
        assertTrue(keys.contains("BorderRadius"));
    }

    /**
     * Tests that getAllKeysList returns an unmodifiable list.
     */
    @Test
    @DisplayName("getAllKeysList should return an unmodifiable list")
    void getAllKeysList_shouldReturnUnmodifiableList() {
        List<String> keys = ThemeType.getAllKeysList();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("NEW"));
        assertThrows(UnsupportedOperationException.class, () -> keys.remove(0));
    }
}
