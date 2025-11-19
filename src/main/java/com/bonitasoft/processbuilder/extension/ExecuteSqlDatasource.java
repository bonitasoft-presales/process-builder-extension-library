package com.bonitasoft.processbuilder.extension;

/**
 * Handles SQL datasource execution for REST API extensions.
 * Provides validation of pagination parameters (page and count).
 * <p>
 * This class validates that pagination parameters are numerical values
 * before executing database queries.
 * </p>
 */
public class ExecuteSqlDatasource {

    /**
     * Validates input parameters for SQL datasource execution.
     * Ensures that pagination parameters 'p' (page) and 'c' (count) are numerical values.
     *
     * @param p the page parameter as a string
     * @param c the count parameter as a string
     * @throws ValidationException if any parameter is not a valid numerical value
     */
    public void validateInputParameters(final String p, final String c) throws ValidationException {
        if (p != null && !p.trim().isEmpty()) {
            QueryParamValidator.validateNumerical("p", p);
        }

        if (c != null && !c.trim().isEmpty()) {
            QueryParamValidator.validateNumerical("c", c);
        }
    }

    /**
     * Executes a SQL query with validated pagination parameters.
     *
     * @param p the page parameter
     * @param c the count parameter
     * @return a result object (placeholder for actual implementation)
     * @throws ValidationException if validation fails
     */
    public Object execute(final String p, final String c) throws ValidationException {
        validateInputParameters(p, c);

        int page = (p != null && !p.trim().isEmpty()) ? Integer.parseInt(p) : 0;
        int count = (c != null && !c.trim().isEmpty()) ? Integer.parseInt(c) : 10;

        // Placeholder for actual SQL execution logic
        return String.format("Executing query with page=%d, count=%d", page, count);
    }
}
