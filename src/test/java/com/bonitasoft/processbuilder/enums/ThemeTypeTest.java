package com.bonitasoft.processbuilder.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    /**
     * Expected number of constants in the ThemeType enum.
     */
    private static final int EXPECTED_CONSTANT_COUNT = 19;

    // -------------------------------------------------------------------------
    // Constant Count and General Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("General Enum Tests")
    class GeneralEnumTests {

        @Test
        @DisplayName("Should contain exactly 19 theme constants")
        void should_contain_expected_number_of_constants() {
            assertEquals(EXPECTED_CONSTANT_COUNT, ThemeType.values().length);
        }

        @ParameterizedTest
        @EnumSource(ThemeType.class)
        @DisplayName("All constants should have non-null and non-empty key and description")
        void all_constants_should_have_valid_key_and_description(ThemeType type) {
            assertThat(type.getKey()).isNotNull().isNotBlank();
            assertThat(type.getDescription()).isNotNull().isNotBlank();
            assertThat(type.name()).isNotNull().isNotBlank();
        }
    }

    // -------------------------------------------------------------------------
    // Brand Identity Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Brand Identity Constants Tests")
    class BrandIdentityTests {

        @Test
        @DisplayName("PRIMARY_COLOR constant should be defined with correct key and description")
        void primaryColor_should_have_correct_values() {
            assertEquals("PRIMARY_COLOR", ThemeType.PRIMARY_COLOR.name());
            assertEquals("PrimaryColor", ThemeType.PRIMARY_COLOR.getKey());
            assertThat(ThemeType.PRIMARY_COLOR.getDescription()).containsIgnoringCase("brand color");
        }

        @Test
        @DisplayName("SAVE_COLOR constant should be defined with correct key and description")
        void saveColor_should_have_correct_values() {
            assertEquals("SAVE_COLOR", ThemeType.SAVE_COLOR.name());
            assertEquals("SaveColor", ThemeType.SAVE_COLOR.getKey());
            assertThat(ThemeType.SAVE_COLOR.getDescription()).containsIgnoringCase("save");
        }

        @Test
        @DisplayName("LOGO_URL constant should be defined with correct key and description")
        void logoUrl_should_have_correct_values() {
            assertEquals("LOGO_URL", ThemeType.LOGO_URL.name());
            assertEquals("LogoUrl", ThemeType.LOGO_URL.getKey());
            assertThat(ThemeType.LOGO_URL.getDescription()).containsIgnoringCase("logo");
        }

        @Test
        @DisplayName("FAVICON_URL constant should be defined with correct key and description")
        void faviconUrl_should_have_correct_values() {
            assertEquals("FAVICON_URL", ThemeType.FAVICON_URL.name());
            assertEquals("FaviconUrl", ThemeType.FAVICON_URL.getKey());
            assertThat(ThemeType.FAVICON_URL.getDescription()).containsIgnoringCase("favicon");
        }

        @Test
        @DisplayName("APP_NAME constant should be defined with correct key and description")
        void appName_should_have_correct_values() {
            assertEquals("APP_NAME", ThemeType.APP_NAME.name());
            assertEquals("AppName", ThemeType.APP_NAME.getKey());
            assertThat(ThemeType.APP_NAME.getDescription()).containsIgnoringCase("application name");
        }
    }

    // -------------------------------------------------------------------------
    // Typography Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Typography Constants Tests")
    class TypographyTests {

        @Test
        @DisplayName("FONT_FAMILY constant should be defined with correct key and description")
        void fontFamily_should_have_correct_values() {
            assertEquals("FONT_FAMILY", ThemeType.FONT_FAMILY.name());
            assertEquals("FontFamily", ThemeType.FONT_FAMILY.getKey());
            assertThat(ThemeType.FONT_FAMILY.getDescription()).containsIgnoringCase("font family");
        }

        @Test
        @DisplayName("HEADING_FONT_FAMILY constant should be defined with correct key and description")
        void headingFontFamily_should_have_correct_values() {
            assertEquals("HEADING_FONT_FAMILY", ThemeType.HEADING_FONT_FAMILY.name());
            assertEquals("HeadingFontFamily", ThemeType.HEADING_FONT_FAMILY.getKey());
            assertThat(ThemeType.HEADING_FONT_FAMILY.getDescription()).containsIgnoringCase("headings");
        }

        @Test
        @DisplayName("BASE_FONT_SIZE constant should be defined with correct key and description")
        void baseFontSize_should_have_correct_values() {
            assertEquals("BASE_FONT_SIZE", ThemeType.BASE_FONT_SIZE.name());
            assertEquals("BaseFontSize", ThemeType.BASE_FONT_SIZE.getKey());
            assertThat(ThemeType.BASE_FONT_SIZE.getDescription()).containsIgnoringCase("font size");
        }
    }

    // -------------------------------------------------------------------------
    // Background Colors Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Background Colors Constants Tests")
    class BackgroundColorsTests {

        @Test
        @DisplayName("BACKGROUND_COLOR constant should be defined with correct key and description")
        void backgroundColor_should_have_correct_values() {
            assertEquals("BACKGROUND_COLOR", ThemeType.BACKGROUND_COLOR.name());
            assertEquals("BackgroundColor", ThemeType.BACKGROUND_COLOR.getKey());
            assertThat(ThemeType.BACKGROUND_COLOR.getDescription()).containsIgnoringCase("background");
        }

        @Test
        @DisplayName("CONTAINER_BACKGROUND constant should be defined with correct key and description")
        void containerBackground_should_have_correct_values() {
            assertEquals("CONTAINER_BACKGROUND", ThemeType.CONTAINER_BACKGROUND.name());
            assertEquals("ContainerBackground", ThemeType.CONTAINER_BACKGROUND.getKey());
            assertThat(ThemeType.CONTAINER_BACKGROUND.getDescription()).containsIgnoringCase("container");
        }

        @Test
        @DisplayName("SECTION_BACKGROUND constant should be defined with correct key and description")
        void sectionBackground_should_have_correct_values() {
            assertEquals("SECTION_BACKGROUND", ThemeType.SECTION_BACKGROUND.name());
            assertEquals("SectionBackground", ThemeType.SECTION_BACKGROUND.getKey());
            assertThat(ThemeType.SECTION_BACKGROUND.getDescription()).containsIgnoringCase("section");
        }
    }

    // -------------------------------------------------------------------------
    // Border & Divider Colors Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Border & Divider Colors Constants Tests")
    class BorderDividerColorsTests {

        @Test
        @DisplayName("BORDER_COLOR constant should be defined with correct key and description")
        void borderColor_should_have_correct_values() {
            assertEquals("BORDER_COLOR", ThemeType.BORDER_COLOR.name());
            assertEquals("BorderColor", ThemeType.BORDER_COLOR.getKey());
            assertThat(ThemeType.BORDER_COLOR.getDescription()).containsIgnoringCase("border");
        }

        @Test
        @DisplayName("DIVIDER_COLOR constant should be defined with correct key and description")
        void dividerColor_should_have_correct_values() {
            assertEquals("DIVIDER_COLOR", ThemeType.DIVIDER_COLOR.name());
            assertEquals("DividerColor", ThemeType.DIVIDER_COLOR.getKey());
            assertThat(ThemeType.DIVIDER_COLOR.getDescription()).containsIgnoringCase("divider");
        }
    }

    // -------------------------------------------------------------------------
    // Text Colors Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Text Colors Constants Tests")
    class TextColorsTests {

        @Test
        @DisplayName("TEXT_COLOR constant should be defined with correct key and description")
        void textColor_should_have_correct_values() {
            assertEquals("TEXT_COLOR", ThemeType.TEXT_COLOR.name());
            assertEquals("TextColor", ThemeType.TEXT_COLOR.getKey());
            assertThat(ThemeType.TEXT_COLOR.getDescription()).containsIgnoringCase("text color");
        }
    }

    // -------------------------------------------------------------------------
    // Status Colors Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Status Colors Constants Tests")
    class StatusColorsTests {

        @Test
        @DisplayName("SUCCESS_COLOR constant should be defined with correct key and description")
        void successColor_should_have_correct_values() {
            assertEquals("SUCCESS_COLOR", ThemeType.SUCCESS_COLOR.name());
            assertEquals("SuccessColor", ThemeType.SUCCESS_COLOR.getKey());
            assertThat(ThemeType.SUCCESS_COLOR.getDescription()).containsIgnoringCase("success");
        }

        @Test
        @DisplayName("WARNING_COLOR constant should be defined with correct key and description")
        void warningColor_should_have_correct_values() {
            assertEquals("WARNING_COLOR", ThemeType.WARNING_COLOR.name());
            assertEquals("WarningColor", ThemeType.WARNING_COLOR.getKey());
            assertThat(ThemeType.WARNING_COLOR.getDescription()).containsIgnoringCase("warning");
        }

        @Test
        @DisplayName("ERROR_COLOR constant should be defined with correct key and description")
        void errorColor_should_have_correct_values() {
            assertEquals("ERROR_COLOR", ThemeType.ERROR_COLOR.name());
            assertEquals("ErrorColor", ThemeType.ERROR_COLOR.getKey());
            assertThat(ThemeType.ERROR_COLOR.getDescription()).containsIgnoringCase("error");
        }

        @Test
        @DisplayName("NEUTRAL_COLOR constant should be defined with correct key and description")
        void neutralColor_should_have_correct_values() {
            assertEquals("NEUTRAL_COLOR", ThemeType.NEUTRAL_COLOR.name());
            assertEquals("NeutralColor", ThemeType.NEUTRAL_COLOR.getKey());
            assertThat(ThemeType.NEUTRAL_COLOR.getDescription()).containsIgnoringCase("neutral");
        }
    }

    // -------------------------------------------------------------------------
    // UI Elements Constants Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("UI Elements Constants Tests")
    class UIElementsTests {

        @Test
        @DisplayName("BORDER_RADIUS constant should be defined with correct key and description")
        void borderRadius_should_have_correct_values() {
            assertEquals("BORDER_RADIUS", ThemeType.BORDER_RADIUS.name());
            assertEquals("BorderRadius", ThemeType.BORDER_RADIUS.getKey());
            assertThat(ThemeType.BORDER_RADIUS.getDescription()).containsIgnoringCase("border radius");
        }
    }

    // -------------------------------------------------------------------------
    // isValid Method Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("isValid Method Tests")
    class IsValidMethodTests {

        @Test
        @DisplayName("isValid should return true for valid uppercase names")
        void isValid_should_return_true_for_valid_uppercase() {
            assertTrue(ThemeType.isValid("PRIMARY_COLOR"));
            assertTrue(ThemeType.isValid("SAVE_COLOR"));
            assertTrue(ThemeType.isValid("BORDER_RADIUS"));
            assertTrue(ThemeType.isValid("NEUTRAL_COLOR"));
        }

        @Test
        @DisplayName("isValid should return true for valid lowercase names")
        void isValid_should_return_true_for_valid_lowercase() {
            assertTrue(ThemeType.isValid("primary_color"));
            assertTrue(ThemeType.isValid("error_color"));
            assertTrue(ThemeType.isValid("container_background"));
        }

        @Test
        @DisplayName("isValid should return true for mixed-case names")
        void isValid_should_return_true_for_mixed_case() {
            assertTrue(ThemeType.isValid("Font_Family"));
            assertTrue(ThemeType.isValid("Background_Color"));
            assertTrue(ThemeType.isValid("Section_Background"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"NON_EXISTENT_TYPE", "INVALID_COLOR", "SECONDARY_COLOR", "INFO_COLOR"})
        @DisplayName("isValid should return false for invalid names")
        void isValid_should_return_false_for_invalid_names(String invalidName) {
            assertFalse(ThemeType.isValid(invalidName));
        }

        @Test
        @DisplayName("isValid should return false for null input")
        void isValid_should_return_false_for_null() {
            assertFalse(ThemeType.isValid(null));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("isValid should return false for null, empty, and whitespace-only input")
        void isValid_should_return_false_for_blank_inputs(String input) {
            assertFalse(ThemeType.isValid(input));
        }

        @Test
        @DisplayName("isValid should return true for whitespace-padded valid names")
        void isValid_should_return_true_for_whitespace_padded_name() {
            assertTrue(ThemeType.isValid("  PRIMARY_COLOR  "));
            assertTrue(ThemeType.isValid("\tSUCCESS_COLOR\t"));
            assertTrue(ThemeType.isValid("  border_radius  "));
        }
    }

    // -------------------------------------------------------------------------
    // getAllData Method Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getAllData Method Tests")
    class GetAllDataMethodTests {

        @Test
        @DisplayName("getAllData should return map with all theme constants")
        void getAllData_should_return_correct_map() {
            Map<String, String> data = ThemeType.getAllData();
            assertEquals(EXPECTED_CONSTANT_COUNT, data.size());

            // Verify brand identity keys
            assertThat(data).containsKey("PrimaryColor");
            assertThat(data).containsKey("SaveColor");
            assertThat(data).containsKey("LogoUrl");
            assertThat(data).containsKey("FaviconUrl");
            assertThat(data).containsKey("AppName");

            // Verify typography keys
            assertThat(data).containsKey("FontFamily");
            assertThat(data).containsKey("HeadingFontFamily");
            assertThat(data).containsKey("BaseFontSize");

            // Verify background keys
            assertThat(data).containsKey("BackgroundColor");
            assertThat(data).containsKey("ContainerBackground");
            assertThat(data).containsKey("SectionBackground");

            // Verify border & divider keys
            assertThat(data).containsKey("BorderColor");
            assertThat(data).containsKey("DividerColor");

            // Verify text color keys
            assertThat(data).containsKey("TextColor");

            // Verify status color keys
            assertThat(data).containsKey("SuccessColor");
            assertThat(data).containsKey("WarningColor");
            assertThat(data).containsKey("ErrorColor");
            assertThat(data).containsKey("NeutralColor");

            // Verify UI element keys
            assertThat(data).containsKey("BorderRadius");
        }

        @Test
        @DisplayName("getAllData should return correct descriptions for each key")
        void getAllData_should_return_correct_descriptions() {
            Map<String, String> data = ThemeType.getAllData();

            assertThat(data.get("PrimaryColor")).containsIgnoringCase("brand color");
            assertThat(data.get("SaveColor")).containsIgnoringCase("save");
            assertThat(data.get("BorderRadius")).containsIgnoringCase("border radius");
        }

        @Test
        @DisplayName("getAllData should return an unmodifiable map")
        void getAllData_should_return_unmodifiable_map() {
            Map<String, String> data = ThemeType.getAllData();
            assertThatThrownBy(data::clear)
                .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> data.put("Test", "Value"))
                .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> data.remove("PrimaryColor"))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // -------------------------------------------------------------------------
    // getAllKeysList Method Tests
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("getAllKeysList Method Tests")
    class GetAllKeysListMethodTests {

        @Test
        @DisplayName("getAllKeysList should return list with all theme keys")
        void getAllKeysList_should_return_correct_list() {
            List<String> keys = ThemeType.getAllKeysList();
            assertEquals(EXPECTED_CONSTANT_COUNT, keys.size());

            // Verify keys from each category
            assertThat(keys).contains(
                "PrimaryColor",
                "SaveColor",
                "FontFamily",
                "BackgroundColor",
                "ContainerBackground",
                "SectionBackground",
                "BorderColor",
                "DividerColor",
                "TextColor",
                "SuccessColor",
                "WarningColor",
                "ErrorColor",
                "NeutralColor",
                "BorderRadius"
            );
        }

        @Test
        @DisplayName("getAllKeysList should return keys in declaration order")
        void getAllKeysList_should_return_keys_in_order() {
            List<String> keys = ThemeType.getAllKeysList();

            // First key should be PrimaryColor (first constant)
            assertEquals("PrimaryColor", keys.get(0));
            // Last key should be BorderRadius (last constant)
            assertEquals("BorderRadius", keys.get(keys.size() - 1));
        }

        @Test
        @DisplayName("getAllKeysList should return an unmodifiable list")
        void getAllKeysList_should_return_unmodifiable_list() {
            List<String> keys = ThemeType.getAllKeysList();
            assertThatThrownBy(() -> keys.add("NEW"))
                .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> keys.remove(0))
                .isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(keys::clear)
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // -------------------------------------------------------------------------
    // valueOf Method Tests (for complete coverage)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("valueOf Method Tests")
    class ValueOfMethodTests {

        @Test
        @DisplayName("valueOf should return correct enum for valid name")
        void valueOf_should_return_correct_enum() {
            assertEquals(ThemeType.PRIMARY_COLOR, ThemeType.valueOf("PRIMARY_COLOR"));
            assertEquals(ThemeType.SAVE_COLOR, ThemeType.valueOf("SAVE_COLOR"));
            assertEquals(ThemeType.BORDER_RADIUS, ThemeType.valueOf("BORDER_RADIUS"));
        }

        @Test
        @DisplayName("valueOf should throw IllegalArgumentException for invalid name")
        void valueOf_should_throw_for_invalid_name() {
            assertThatThrownBy(() -> ThemeType.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
