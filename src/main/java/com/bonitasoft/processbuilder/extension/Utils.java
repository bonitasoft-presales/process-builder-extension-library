package com.bonitasoft.processbuilder.extension;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing common String manipulation methods, focusing on
 * normalization and case formatting.
 * <p>
 * This class is designed to be non-instantiable and should only be accessed
 * via static methods.
 * </p>
 *
 * @author Bonitasoft
 * @since 1.0
 */
public final class Utils {

        /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * @throws UnsupportedOperationException always, to enforce the utility pattern.
     */
    private Utils() {
        throw new UnsupportedOperationException("This is a " + this.getClass().getSimpleName() + " class and cannot be instantiated.");
    }

   
    /**
	 * Calculates and logs the elapsed time from a given start point up to the moment of the call.
	 * <p>
	 * The total execution time (the difference between the current time and {@code startTime})
	 * is broken down and displayed in minutes, seconds, and milliseconds via the log
	 * at the {@code INFO} level.
	 *
	 * @param startTime The initial time in milliseconds (typically obtained using {@code System.currentTimeMillis()})
	 * from which the duration is calculated.
	 * @param name      A descriptive identifier or name of the operation/task for which
	 * the time is being measured (used within the log message).
	 * @see System#currentTimeMillis()
	 */
	public static void logElapsedTime(long startTime, String name) {
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		
		// Convert milliseconds to minutes, seconds, and milliseconds
		long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60;

		long milliseconds = elapsedTime % 1000;
		
		// Assuming 'LOGGER' is defined and available in this context
		LOGGER.info("Elapsed time - {}: {}m {}s {}ms", name, minutes, seconds, milliseconds);
	}
}