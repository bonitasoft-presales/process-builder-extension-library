package com.bonitasoft.processbuilder.extension;

/**
 * Handles process instance operations for REST API extensions.
 * Provides methods for validating and processing process instance identifiers.
 * <p>
 * This class is responsible for validating caseId and id parameters as long values
 * and ensuring they are within valid ranges.
 * </p>
 */
public class ProcessInstance {

    /**
     * Validates and retrieves a process instance by case ID.
     *
     * @param caseIdParam the case ID parameter as a string
     * @return the validated case ID as a long
     * @throws ValidationException if the caseId is not a valid long
     */
    public long getByCaseId(final String caseIdParam) throws ValidationException {
        QueryParamValidator.validateMandatoryLong("caseId", caseIdParam);
        return Long.parseLong(caseIdParam);
    }

    /**
     * Validates and retrieves a process instance by ID.
     *
     * @param idParam the ID parameter as a string
     * @return the validated ID as a long
     * @throws ValidationException if the id is not a valid long
     */
    public long getById(final String idParam) throws ValidationException {
        QueryParamValidator.validateMandatoryLong("id", idParam);
        return Long.parseLong(idParam);
    }
}
