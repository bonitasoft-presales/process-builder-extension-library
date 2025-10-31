package com.bonitasoft.processbuilder.records;

/**
 * A record representing the user who executed a specific human task.
 *
 * @param id The unique identifier of the user who executed the task.
 * @param userName The username of the task executor.
 * @param fullName The full name (first name + last name) of the task executor.
 */
public record TaskExecutor(Long id, String userName, String fullName) {
    /**
     * Compact constructor to perform validation or canonicalization, if needed.
     * Currently, it serves as the default constructor provided by the record.
     * @param id The unique identifier of the user who executed the task.
     * @param userName The username of the task executor.
     * @param fullName The full name (first name + last name) of the task executor.
     */
    public TaskExecutor {
    }

}
