package com.bonitasoft.processbuilder.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Defines the valid SMTP configuration attributes for email server settings.
 * This enumeration is typically used as the source for the 'SMTP' master data,
 * containing all necessary parameters to configure email notifications.
 *
 * @author Bonitasoft
 * @since 1.0
 */
public enum SmtpType {

    /**
     * The SMTP server hostname or IP address used for sending emails.
     */
    SMTP_HOST("SmtpHost", "The SMTP server hostname or IP address (e.g., smtp.gmail.com, smtp.office365.com)."),

    /**
     * The port number used to connect to the SMTP server.
     */
    SMTP_PORT("SmtpPort", "The port number for SMTP connection (e.g., 25, 465 for SSL, 587 for STARTTLS)."),

    /**
     * The default sender email address for outgoing messages.
     */
    FROM("From", "The default sender email address (From) used in outgoing email notifications."),

    /**
     * Indicates whether SSL encryption is enabled for the SMTP connection.
     */
    SSL("Ssl", "Enables SSL (Secure Sockets Layer) encryption for secure SMTP communication. Typically used with port 465."),

    /**
     * Indicates whether STARTTLS encryption is enabled for the SMTP connection.
     */
    STARTTLS("StartTls", "Enables STARTTLS encryption, which upgrades an insecure connection to a secure one. Typically used with port 587."),

    /**
     * Indicates whether to trust the server certificate without validation.
     */
    TRUST_CERTIFICATE("TrustCertificate",
            "When enabled, accepts the server certificate without strict validation. "
                    + "Use with caution in production environments."),

    /**
     * The username for SMTP server authentication.
     */
    USERNAME("Username", "The username or email address used for SMTP server authentication."),

    /**
     * The password for SMTP server authentication.
     */
    PASSWORD("Password", "The password or app-specific password used for SMTP server authentication."),

    /**
     * The HTML template for email body content.
     * <p>
     * This type stores the email template that wraps the actual email content.
     * The template must contain a {@code {{content}}} placeholder where the
     * email body will be inserted. Example template:
     * </p>
     * <pre>{@code
     * <p style="text-align: center;">
     *   <img src="logo-url" style="width: 300px;">
     * </p>
     * <p>{{content}}</p>
     * }</pre>
     */
    EMAILTEMPLATE("EmailTemplate",
            "The HTML template for email notifications. Must contain {{content}} placeholder "
                    + "where the email body content will be inserted.");

    private final String key;
    private final String description;

    /**
     * Private constructor for the enumeration.
     * @param key The technical key used for mapping.
     * @param description A human-readable description of the SMTP attribute.
     */
    SmtpType(String key, String description) {
        this.key = key;
        this.description = description;
    }

    /**
     * Returns the unique string key associated with this SMTP attribute.
     * @return The SMTP attribute key (e.g., "SmtpHost").
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the description of this SMTP configuration attribute.
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
            SmtpType.valueOf(input.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retrieves all SMTP configuration attributes as a read-only Map where the key is the technical key 
     * and the value is the description.
     * @return A map containing all SMTP attribute data (Key -> Description).
     */
    public static Map<String, String> getAllData() {
        Map<String, String> data = 
            Arrays.stream(values())
            .collect(Collectors.toMap(
                SmtpType::getKey, 
                SmtpType::getDescription, 
                (oldValue, newValue) -> oldValue, 
                LinkedHashMap::new 
            ));
        
        return Collections.unmodifiableMap(data);
    }

    /**
     * Retrieves all technical keys as a read-only List of Strings.
     * @return A list containing all SMTP attribute keys.
     */
    public static List<String> getAllKeysList() {
        return Arrays.stream(values())
            .map(SmtpType::getKey)
            .collect(Collectors.toUnmodifiableList());
    }
}