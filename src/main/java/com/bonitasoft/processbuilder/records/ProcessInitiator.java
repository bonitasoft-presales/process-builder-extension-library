package com.bonitasoft.processbuilder.records;

/**
 * A record representing the process initiator details.
 *
 * @param id The unique identifier of the user who initiated the process.
 * @param userName The username of the process initiator.
 * @param fullName The full name (first name + last name) of the process initiator.
 */
public record ProcessInitiator(Long id, String userName, String fullName) {
    /**
     * Compact constructor to perform validation or canonicalization, if needed.
     * Currently, it serves as the default constructor provided by the record.
     * @param id The unique identifier of the user who initiated the process.
     * @param userName The username of the process initiator.
     * @param fullName The full name (first name + last name) of the process initiator.
     */
    public ProcessInitiator {
    }

}
