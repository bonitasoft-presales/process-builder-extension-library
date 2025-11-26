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
    PRIMARY_COLOR("PrimaryColor", "The main brand color used for primary actions, headers, and key UI elements (e.g., #1976D2)."),

    /**
     * The secondary brand color used for accents and highlights.
     */
    SECONDARY_COLOR("SecondaryColor", "The secondary brand color used for accents, highlights, and secondary actions (e.g., #FF9800)."),

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
    BACKGROUND_COLOR("BackgroundColor", "The main background color for the application body (e.g., #FFFFFF, #F5F5F5)."),

    /**
     * The background color for the header section.
     */
    HEADER_BACKGROUND("HeaderBackground", "The background color for the application header/navbar (e.g., #1976D2)."),

    /**
     * The background color for the sidebar/navigation menu.
     */
    SIDEBAR_BACKGROUND("SidebarBackground", "The background color for the sidebar or navigation menu (e.g., #263238)."),

    /**
     * The background color for the footer section.
     */
    FOOTER_BACKGROUND("FooterBackground", "The background color for the application footer (e.g., #212121)."),

    // ==================== Text Colors ====================

    /**
     * The primary text color used for general content.
     */
    TEXT_COLOR("TextColor", "The primary text color for body content and general text (e.g., #212121, #333333)."),

    /**
     * The text color used on primary colored backgrounds.
     */
    TEXT_ON_PRIMARY("TextOnPrimary", "The text color used on primary color backgrounds for contrast (e.g., #FFFFFF)."),

    /**
     * The color used for hyperlinks.
     */
    LINK_COLOR("LinkColor", "The color for hyperlinks and clickable text elements (e.g., #1565C0)."),

    // ==================== Status Colors ====================

    /**
     * The color indicating success states and positive feedback.
     */
    SUCCESS_COLOR("SuccessColor", "The color for success messages, confirmations, and positive indicators (e.g., #4CAF50)."),

    /**
     * The color indicating warning states and cautionary feedback.
     */
    WARNING_COLOR("WarningColor", "The color for warning messages and cautionary indicators (e.g., #FF9800)."),

    /**
     * The color indicating error states and negative feedback.
     */
    ERROR_COLOR("ErrorColor", "The color for error messages, validation failures, and critical alerts (e.g., #F44336)."),

    /**
     * The color indicating informational states.
     */
    INFO_COLOR("InfoColor", "The color for informational messages and neutral notifications (e.g., #2196F3)."),

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
            Arrays.stream(ThemeType.values())
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
        return Arrays.stream(ThemeType.values())
            .map(ThemeType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}