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
		long elapsedTime = calculateElapsedTime(startTime);
		logElapsedTimeByElapsedTime(elapsedTime, name);
	}

	/**
	 * Calculates the elapsed time from a given start point up to the current moment.
	 * <p>
	 * This method provides a simple way to measure execution duration by comparing
	 * the provided start time with the current system time.
	 *
	 * @param startTime The initial time in milliseconds (typically obtained using 
	 *                  {@code System.currentTimeMillis()}) from which the duration is calculated.
	 * @return The elapsed time in milliseconds.
	 * @see System#currentTimeMillis()
	 */
	public static long calculateElapsedTime(long startTime) {
		return calculateElapsedTime(startTime, System.currentTimeMillis());
	}

	/**
	 * Calculates the elapsed time between two given time points.
	 * <p>
	 * This method computes the difference between the end time and start time,
	 * returning the result in milliseconds.
	 *
	 * @param startTime The initial time in milliseconds (typically obtained using 
	 *                  {@code System.currentTimeMillis()}).
	 * @param endTime   The final time in milliseconds (typically obtained using 
	 *                  {@code System.currentTimeMillis()}).
	 * @return The elapsed time in milliseconds (endTime - startTime).
	 * @see System#currentTimeMillis()
	 */
	public static long calculateElapsedTime(long startTime, long endTime) {
		return endTime - startTime;
	}

	/**
	 * Logs the given elapsed time, formatted as minutes, seconds, and milliseconds.
	 * <p>
	 * This method is useful when the elapsed time has already been calculated
	 * and only needs to be logged with a descriptive name.
	 *
	 * @param elapsedTime The pre-calculated elapsed time in milliseconds.
	 * @param name        A descriptive identifier or name of the operation/task
	 *                    for which the time was measured (used within the log message).
	 */
	public static void logElapsedTimeByElapsedTime(long elapsedTime, String name) {
		String formattedTime = formatElapsedTime(elapsedTime);
		LOGGER.info("Elapsed time - {}: {}", name, formattedTime);
	}

	/**
	 * Formats elapsed time as a human-readable string with minutes, seconds, and milliseconds.
	 * <p>
	 * This method extracts the time components and formats them in a consistent pattern.
	 *
	 * @param elapsedTime The elapsed time in milliseconds.
	 * @return A formatted string in the pattern "Xm Ys Zms".
	 */
	public static String formatElapsedTime(long elapsedTime) {
		long minutes = extractMinutes(elapsedTime);
		long seconds = extractSeconds(elapsedTime);
		long milliseconds = extractMilliseconds(elapsedTime);
		return String.format("%dm %ds %dms", minutes, seconds, milliseconds);
	}

	/**
	 * Extracts the minutes component from an elapsed time in milliseconds.
	 *
	 * @param elapsedTimeMillis The elapsed time in milliseconds.
	 * @return The number of complete minutes.
	 */
	public static long extractMinutes(long elapsedTimeMillis) {
		return TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis);
	}

	/**
	 * Extracts the seconds component (0-59) from an elapsed time in milliseconds.
	 * <p>
	 * This returns only the seconds portion after extracting complete minutes,
	 * using modulo 60 to ensure the result is within the 0-59 range.
	 *
	 * @param elapsedTimeMillis The elapsed time in milliseconds.
	 * @return The seconds component (0-59).
	 */
	public static long extractSeconds(long elapsedTimeMillis) {
		return TimeUnit.MILLISECONDS.toSeconds(elapsedTimeMillis) % 60;
	}

	/**
	 * Extracts the milliseconds component (0-999) from an elapsed time.
	 * <p>
	 * This returns only the milliseconds portion after extracting complete seconds,
	 * using modulo 1000 to ensure the result is within the 0-999 range.
	 *
	 * @param elapsedTimeMillis The elapsed time in milliseconds.
	 * @return The milliseconds component (0-999).
	 */
	public static long extractMilliseconds(long elapsedTimeMillis) {
		return elapsedTimeMillis % 1000;
	}

	/**
	 * Calculates and logs the elapsed time from a given start point up to the current moment.
	 * <p>
	 * This is a convenience method that combines {@link #calculateElapsedTime(long)} 
	 * and {@link #logElapsedTime(long, String)} into a single call. The total execution 
	 * time is broken down and displayed in minutes, seconds, and milliseconds via the 
	 * log at the {@code INFO} level.
	 *
	 * @param startTime The initial time in milliseconds (typically obtained using 
	 *                  {@code System.currentTimeMillis()}) from which the duration is calculated.
	 * @param name      A descriptive identifier or name of the operation/task for which
	 *                  the time is being measured (used within the log message).
	 * @return The elapsed time in milliseconds.
	 * @see #calculateElapsedTime(long)
	 * @see #logElapsedTime(long, String)
	 * @see System#currentTimeMillis()
	 */
	public static long logAndGetElapsedTime(long startTime, String name) {
		long elapsedTime = calculateElapsedTime(startTime);
		logElapsedTimeByElapsedTime(elapsedTime, name);
		return elapsedTime;
	}
}