package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Defines the supported content types for REST service requests and responses.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum RestContentType {

    /**
     * JSON content type (application/json).
     */
    JSON("application/json", "JSON format"),

    /**
     * XML content type (application/xml).
     */
    XML("application/xml", "XML format"),

    /**
     * Plain text content type (text/plain).
     */
    TEXT_PLAIN("text/plain", "Plain text format"),

    /**
     * HTML content type (text/html).
     */
    TEXT_HTML("text/html", "HTML format"),

    /**
     * Form URL encoded content type (application/x-www-form-urlencoded).
     */
    FORM_URLENCODED("application/x-www-form-urlencoded", "URL-encoded form data"),

    /**
     * Multipart form data content type (multipart/form-data).
     */
    MULTIPART_FORM_DATA("multipart/form-data", "Multipart form data for file uploads"),

    /**
     * Binary/octet-stream content type (application/octet-stream).
     */
    BINARY("application/octet-stream", "Binary data"),

    /**
     * PDF content type (application/pdf).
     */
    PDF("application/pdf", "PDF document");

    private final String mimeType;
    private final String description;

    /**
     * Private constructor for the enumeration.
     *
     * @param mimeType    The MIME type string
     * @param description A human-readable description
     */
    RestContentType(String mimeType, String description) {
        this.mimeType = mimeType;
        this.description = description;
    }

    /**
     * Gets the MIME type string.
     *
     * @return The MIME type (e.g., "application/json")
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Alias for getMimeType() to maintain consistency with other enums.
     *
     * @return The MIME type string
     */
    public String getKey() {
        return mimeType;
    }

    /**
     * Gets a brief description of the content type.
     *
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Checks if a given MIME type corresponds to a valid content type.
     *
     * @param input The MIME type to validate (case-insensitive)
     * @return {@code true} if the MIME type is valid
     */
    public static boolean isValid(String input) {
        return fromMimeType(input).isPresent();
    }

    /**
     * Finds a content type by its MIME type (case-insensitive).
     *
     * @param mimeType The MIME type to search for
     * @return Optional containing the matching content type, or empty if not found
     */
    public static Optional<RestContentType> fromMimeType(String mimeType) {
        if (mimeType == null || mimeType.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalized = mimeType.trim().toLowerCase();
        // Handle content type with charset (e.g., "application/json; charset=utf-8")
        if (normalized.contains(";")) {
            normalized = normalized.substring(0, normalized.indexOf(";")).trim();
        }
        final String searchType = normalized;
        return Arrays.stream(values())
                .filter(type -> type.getMimeType().equalsIgnoreCase(searchType))
                .findFirst();
    }

    /**
     * Retrieves all content types as a read-only Map.
     *
     * @return A map containing all content type data (MimeType -> Description)
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = Arrays.stream(values())
                .collect(Collectors.toMap(
                        RestContentType::getMimeType,
                        RestContentType::getDescription,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all MIME types as a read-only List.
     *
     * @return A list containing all MIME type strings
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
                .map(RestContentType::getMimeType)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Checks if this content type is text-based (can be logged as string).
     *
     * @return {@code true} if the content is text-based
     */
    public boolean isTextBased() {
        return this == JSON || this == XML || this == TEXT_PLAIN || this == TEXT_HTML;
    }

    /**
     * Checks if this content type is JSON.
     *
     * @return {@code true} if this is JSON content type
     */
    public boolean isJson() {
        return this == JSON;
    }

    /**
     * Checks if this content type is XML.
     *
     * @return {@code true} if this is XML content type
     */
    public boolean isXml() {
        return this == XML;
    }
}
