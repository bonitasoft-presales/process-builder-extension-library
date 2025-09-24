package com.bonitasoft.processbuilder.enums;

/**
 * Defines the valid option types for a process.
 * <p>
 * This enumeration is used to categorize different components within a process,
 * such as parameters, users, inputs, steps, and status. It improves code clarity
 * and reduces the risk of errors from using hard-coded strings.
 * </p>
 */
public enum ProcessOptionType {
    /**
     * Represents a process parameter.
     */
    PARAMETER,
    
    /**
     * Represents a user within a process.
     */
    USERS,
    
    /**
     * Represents the inputs of a process.
     */
    INPUTS,
    
    /**
     * Represents a step in the process workflow.
     */
    STEPS,
    
    /**
     * Represents the status of the process.
     */
    STATUS
}