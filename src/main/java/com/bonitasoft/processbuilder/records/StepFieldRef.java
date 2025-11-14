package com.bonitasoft.processbuilder.records;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A record representing a parsed reference to a step and field.
 * <p>
 * This record is typically created by parsing a string in the format "step_xxx:field_yyy"
 * where stepRef and fieldRef are the respective identifiers.
 * </p>
 *
 * @param stepRef The step reference identifier (e.g., "step_xxx")
 * @param fieldRef The field reference identifier (e.g., "field_yyy")
 * @author Bonitasoft
 * @since 1.0
 */
public record StepFieldRef(
    String stepRef,
    String fieldRef
) {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StepFieldRef.class);
    /**
     * Compact constructor for validation.
     *
     * @param stepRef The step reference identifier
     * @param fieldRef The field reference identifier
     */
    public StepFieldRef {
    }

    /**
     * Parses a string in the format "step_xxx:field_yyy" and creates a StepFieldRef record.
     * <p>
     * The expected format is two parts separated by a colon (:), where the first part
     * is the step reference and the second part is the field reference.
     * Both parts will be trimmed of leading and trailing whitespace.
     * </p>
     *
     * @param stepFieldRef The string to parse in format "step_xxx:field_yyy"
     * @return A StepFieldRef record with parsed stepRef and fieldRef, or {@code null} if parsing fails
     */
    public static StepFieldRef parse(String stepFieldRef) {
        // Validate input
        if (stepFieldRef == null || stepFieldRef.isEmpty()) {
            LOGGER.warn("Invalid stepFieldRef: input is null or empty. Expected format 'step_xxx:field_yyy'");
            return null;
        }

        if (!stepFieldRef.contains(":")) {
            LOGGER.warn("Invalid stepFieldRef format: '{}'. Expected format 'step_xxx:field_yyy' with colon separator", stepFieldRef);
            return null;
        }

        // Parse the format "step_xxx:field_yyy"
        String[] parts = stepFieldRef.split(":", 2);
        String stepRef = parts[0].trim();
        String fieldRef = parts[1].trim();

        // Validate parsed parts
        if (stepRef.isEmpty() || fieldRef.isEmpty()) {
            LOGGER.warn("Invalid stepFieldRef format: '{}'. Both stepRef and fieldRef must be non-empty after parsing", stepFieldRef);
            return null;
        }

        LOGGER.debug("Successfully parsed stepFieldRef: '{}' -> stepRef='{}', fieldRef='{}'", stepFieldRef, stepRef, fieldRef);

        return new StepFieldRef(stepRef, fieldRef);
    }
}
