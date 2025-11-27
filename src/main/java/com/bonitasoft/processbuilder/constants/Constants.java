package com.bonitasoft.processbuilder.constants;


/**
 * Utility class containing all application-wide constant values.
 * <p>
 * This class is designed to be non-instantiable, ensuring it is used only
 * for static access to its constants.
 * </p>
 *
 */
public final class Constants { // Marked as final to prevent inheritance

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, as this class should not be instantiated.
     */
    private Constants() {
        // It's a common practice to make utility class constructors throw an exception
        // to prevent misuse via reflection, although 'final' helps prevent common instantiation.
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

    /**
     * Constant representing a general test string.
     */
    public static final String TEST = "Test";

    /**
     * Constant representing an empty string ("").
     * <p>
     * Use this constant instead of directly typing {@code ""}.
     * </p>
     */
    public static final String EMPTY = "";

    public static final String PB_USER_PROFILE = "PB User";
    public static final String PB_ADMINISTRATOR_PROFILE = "PB Administrator";
    public static final String PB_PROCESS_MANAGER_PROFILE = "PB Process Manager";

}

