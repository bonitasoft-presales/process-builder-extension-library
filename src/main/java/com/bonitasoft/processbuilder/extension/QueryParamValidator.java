package com.bonitasoft.processbuilder.extension;

/**
 * Utility class for validating query parameters in REST API extensions.
 * Provides methods for validating mandatory long parameters and numerical values.
 * <p>
 * This class follows the utility pattern with a private constructor and static methods only.
 * </p>
 */
public class QueryParamValidator {

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private QueryParamValidator() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Validates that a mandatory query parameter is a valid long value.
     *
     * @param paramName the name of the parameter being validated
     * @param paramValue the string value of the parameter to validate
     * @throws ValidationException if the parameter is null, empty, or not a valid long
     */
    public static void validateMandatoryLong(final String paramName, final String paramValue) throws ValidationException {
        if (paramValue == null || paramValue.trim().isEmpty()) {
            throw new ValidationException(String.format("the parameter %s is mandatory", paramName));
        }

        try {
            Long.parseLong(paramValue);
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format("the parameter %s should be a long", paramName));
        }
    }

    /**
     * Validates that a query parameter is a numerical value (integer).
     *
     * @param paramName the name of the parameter being validated
     * @param paramValue the string value of the parameter to validate
     * @throws ValidationException if the parameter is not a valid numerical value
     */
    public static void validateNumerical(final String paramName, final String paramValue) throws ValidationException {
        if (paramValue == null || paramValue.trim().isEmpty()) {
            throw new ValidationException(String.format("the parameter %s is mandatory", paramName));
        }

        try {
            Integer.parseInt(paramValue);
        } catch (NumberFormatException e) {
            throw new ValidationException(String.format("the parameter %s should be a numerical value", paramName));
        }
    }
}
