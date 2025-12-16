package com.bonitasoft.processbuilder.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Utility class for safely retrieving and handling configuration values.
 * <p>
 * This class provides methods to lookup configuration values with proper logging,
 * error handling, and support for sensitive data masking (e.g., passwords).
 * </p>
 * <p>
 * The configuration retrieval is decoupled from the data source through functional interfaces,
 * allowing integration with any DAO or configuration provider.
 * </p>
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Example 1: Basic configuration lookup
 * String smtpHost = ConfigurationUtils.lookupConfigurationValue(
 *     SmtpType.SMTP_HOST.getKey(),
 *     ConfigurationType.SMTP.getKey(),
 *     () -> pBConfigurationDAO.findByFullNameAndRefEntityTypeName(
 *         SmtpType.SMTP_HOST.getKey(),
 *         ConfigurationType.SMTP.getKey()
 *     ).getConfigValue(),
 *     "localhost",
 *     false  // not sensitive
 * );
 *
 * // Example 2: Sensitive value lookup (password - value will be masked in logs)
 * String password = ConfigurationUtils.lookupConfigurationValue(
 *     SmtpType.PASSWORD.getKey(),
 *     ConfigurationType.SMTP.getKey(),
 *     () -> pBConfigurationDAO.findByFullNameAndRefEntityTypeName(
 *         SmtpType.PASSWORD.getKey(),
 *         ConfigurationType.SMTP.getKey()
 *     ).getConfigValue(),
 *     "",
 *     true  // sensitive - will be masked in logs
 * );
 * }</pre>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class ConfigurationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationUtils.class);

    /**
     * Mask used to hide sensitive values in log output.
     */
    public static final String MASKED_VALUE = "********";

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern
     */
    private ConfigurationUtils() {
        throw new UnsupportedOperationException(
            "This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated."
        );
    }

    /**
     * Looks up a configuration value using a provided supplier, with support for sensitive data masking.
     * <p>
     * This method encapsulates the common pattern of:
     * </p>
     * <ol>
     *   <li>Logging the start of the lookup operation</li>
     *   <li>Executing the configuration retrieval via the supplier</li>
     *   <li>Validating the retrieved value</li>
     *   <li>Returning the value or a default if not found/empty</li>
     *   <li>Masking sensitive values in log output</li>
     *   <li>Logging the completion of the operation</li>
     * </ol>
     *
     * @param fullNameKey          the configuration key name (e.g., "SmtpHost", "Password")
     * @param entityTypeKey        the entity type for logging context (e.g., "SMTP", "DATABASE")
     * @param configValueSupplier  a supplier that retrieves the configuration value;
     *                             may return null if configuration not found
     * @param defaultValue         the default value to return if configuration is not found or empty
     * @param isSensitive          if true, the actual value will be masked in log output
     * @return the configuration value if found and not empty, otherwise the default value
     * @throws RuntimeException if the supplier throws an exception during retrieval
     */
    public static String lookupConfigurationValue(
            String fullNameKey,
            String entityTypeKey,
            Supplier<String> configValueSupplier,
            String defaultValue,
            boolean isSensitive) {

        LOGGER.info("--- [ConfigurationUtils] Starting {} configuration lookup ---", fullNameKey);

        // Validate inputs
        if (fullNameKey == null || fullNameKey.isBlank()) {
            LOGGER.warn("fullNameKey is null or blank. Returning default value.");
            return defaultValue;
        }

        if (configValueSupplier == null) {
            LOGGER.warn("configValueSupplier is null for {}. Returning default value: {}",
                       fullNameKey, maskIfSensitive(defaultValue, isSensitive));
            return defaultValue;
        }

        try {
            LOGGER.info("Querying configuration with fullName: {} and entityType: {}",
                       fullNameKey, entityTypeKey);

            // Execute the lookup via supplier
            String configValue = configValueSupplier.get();

            // Validate existence
            if (configValue == null || configValue.trim().isEmpty()) {
                LOGGER.warn("{} configuration value is null or empty. Returning default value: {}",
                           fullNameKey, maskIfSensitive(defaultValue, isSensitive));
                return defaultValue;
            }

            // Return the trimmed value
            String value = configValue.trim();
            LOGGER.info("Successfully retrieved {}: {}",
                       fullNameKey, maskIfSensitive(value, isSensitive));
            return value;

        } catch (Exception e) {
            LOGGER.error("Unexpected error while querying configuration for {}. Details: {}",
                        fullNameKey, e.getMessage());
            throw new RuntimeException(
                "Failed to retrieve " + fullNameKey + " configuration due to an unexpected error.", e);

        } finally {
            LOGGER.info("--- [ConfigurationUtils] Finished {} configuration lookup ---", fullNameKey);
        }
    }

    /**
     * Looks up a configuration value without entity type context.
     * <p>
     * This is a convenience overload for cases where entity type logging is not needed.
     * </p>
     *
     * @param fullNameKey          the configuration key name
     * @param configValueSupplier  a supplier that retrieves the configuration value
     * @param defaultValue         the default value to return if configuration is not found
     * @param isSensitive          if true, the actual value will be masked in log output
     * @return the configuration value if found and not empty, otherwise the default value
     * @throws RuntimeException if the supplier throws an exception during retrieval
     */
    public static String lookupConfigurationValue(
            String fullNameKey,
            Supplier<String> configValueSupplier,
            String defaultValue,
            boolean isSensitive) {
        return lookupConfigurationValue(fullNameKey, null, configValueSupplier, defaultValue, isSensitive);
    }

    /**
     * Masks a value if it is marked as sensitive.
     *
     * @param value       the value to potentially mask
     * @param isSensitive whether the value should be masked
     * @return the masked value if sensitive, otherwise the original value
     */
    public static String maskIfSensitive(String value, boolean isSensitive) {
        if (isSensitive && value != null && !value.isEmpty()) {
            return MASKED_VALUE;
        }
        return value;
    }
}
