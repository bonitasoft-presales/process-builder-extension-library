package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid theme configuration attributes for application visual customization.
 * This enumeration is typically used as the source for the 'Theme' master data,
 * containing all necessary parameters to customize the application's look and feel.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum ThemeType {

    // ==================== Brand Identity ====================

    /**
     * The primary brand color used throughout the application.
     */
    PRIMARY_COLOR("PrimaryColor", "The main brand color used for primary actions, headers, and key UI elements."),

    /**
     * The color used for save actions.
     */
    SAVE_COLOR("SaveColor", "The color used for save buttons and related actions."),

    /**
     * The URL or base64-encoded data of the application logo.
     */
    LOGO_URL("LogoUrl", "The URL or path to the application logo image displayed in the header and login page."),

    /**
     * The URL or base64-encoded data of the favicon.
     */
    FAVICON_URL("FaviconUrl", "The URL or path to the favicon displayed in browser tabs."),

    /**
     * The application name displayed in the header and title.
     */
    APP_NAME("AppName", "The application name displayed in the header, title bar, and branding areas."),

    // ==================== Typography ====================

    /**
     * The primary font family used for general text.
     */
    FONT_FAMILY("FontFamily", "The primary font family for body text (e.g., 'Roboto', 'Open Sans', 'Arial')."),

    /**
     * The font family used for headings.
     */
    HEADING_FONT_FAMILY("HeadingFontFamily", "The font family used for headings and titles (e.g., 'Montserrat', 'Poppins')."),

    /**
     * The base font size for the application.
     */
    BASE_FONT_SIZE("BaseFontSize", "The base font size in pixels used as reference for all text elements (e.g., 14, 16)."),

    // ==================== Background Colors ====================

    /**
     * The main background color of the application.
     */
    BACKGROUND_COLOR("BackgroundColor", "The main background color for the application body."),

    /**
     * The background color for containers and cards.
     */
    CONTAINER_BACKGROUND("ContainerBackground", "The background color for containers, cards, and content areas."),

    /**
     * The background color for highlighted sections.
     */
    SECTION_BACKGROUND("SectionBackground", "The background color for highlighted or selected sections."),

    // ==================== Border & Divider Colors ====================

    /**
     * The color used for borders.
     */
    BORDER_COLOR("BorderColor", "The color for borders and container outlines."),

    /**
     * The color used for dividers.
     */
    DIVIDER_COLOR("DividerColor", "The color for dividers and separators."),

    // ==================== Text Colors ====================

    /**
     * The primary text color used for general content.
     */
    TEXT_COLOR("TextColor", "The primary text color for body content and general text."),

    // ==================== Status Colors ====================

    /**
     * The color indicating success states and positive feedback.
     */
    SUCCESS_COLOR("SuccessColor", "The color for success messages, validation, and positive indicators."),

    /**
     * The color indicating warning states and cautionary feedback.
     */
    WARNING_COLOR("WarningColor", "The color for warning messages and cautionary indicators."),

    /**
     * The color indicating error states and negative feedback.
     */
    ERROR_COLOR("ErrorColor", "The color for error messages, delete actions, and critical alerts."),

    /**
     * The color indicating neutral or inactive states.
     */
    NEUTRAL_COLOR("NeutralColor", "The color for neutral, inactive, or unknown states."),

    // ==================== UI Elements ====================

    /**
     * The border radius used for buttons, cards, and input fields.
     */
    BORDER_RADIUS("BorderRadius", "The border radius in pixels for rounded corners on buttons, cards, and inputs (e.g., 4, 8).");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the theme attribute.
     */
    ThemeType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this theme attribute.
     * @return The theme attribute key (e.g., "PrimaryColor").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this theme configuration attribute.
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given string corresponds to a valid enum constant, ignoring case and leading/trailing spaces.
     * @param input The string to validate.
     * @return {@code true} if the string is a valid enum constant, {@code false} otherwise.
     */
    public static boolean isValid(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        try {
            ThemeType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all theme configuration attributes as a read-only Map where the key is the technical key
     * and the value is the description.
     * @return A map containing all theme attribute data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data =
            Arrays.stream(values())
            .collect(Collectors.toMap(
                ThemeType::getKey,
                ThemeType::getDescription,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new
            ));

        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all theme attribute keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(ThemeType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}