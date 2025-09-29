package com.bonitasoft.processbuilder.extension;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bonitasoft.processbuilder.extension.ProcessUtils.ProcessInitiator;

/**
 * Utility class providing methods to automatically set creation and modification
 * metadata on Business Data Model (BDM) objects using Java Reflection.
 *
 * <p>This class assumes that all target BDM objects implement the following methods:</p>
 * <ul>
 * <li>{@code getCreationDate()}: returns OffsetDateTime</li>
 * <li>{@code setCreationDate(OffsetDateTime)}</li>
 * <li>{@code setCreatorId(Long)}</li>
 * <li>{@code setCreatorName(String)}</li>
 * <li>{@code setModificationDate(OffsetDateTime)}</li>
 * <li>{@code setModifierId(Long)}</li>
 * <li>{@code setModifierName(String)}</li>
 * </ul>
 * 
 */
public class BDMAuditUtils {

    /**
     * A logger for this class, used to record log messages and provide debugging information.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BDMAuditUtils.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private BDMAuditUtils() {
        // Utility classes should not be instantiated.
    }

    /**
     * Applies audit metadata (creation or modification) to a BDM object.
     * If the object passed is {@code null}, it is instantiated generically using its default constructor.
     *
     * @param <T> The generic type of the BDM object.
     * @param bdmObject The existing BDM object (may be {@code null}).
     * @param clazz The class of the BDM (e.g., {@code PBCategory.class}). Required for generic instantiation if {@code bdmObject} is {@code null}.
     * @param initiator The user performing the action.
     * @param persistenceId The persistence ID of the object (used for logging purposes).
     * @return The updated (or newly created) BDM object of type T.
     * @throws RuntimeException If instantiation fails (missing default constructor) or if a reflection error occurs (missing setter/getter methods)
     */
    public static <T> T createOrUpdateAuditData(T bdmObject, Class<T> clazz, ProcessInitiator initiator, Long persistenceId) {
        
        T targetObject = bdmObject;
        String objectName = clazz.getSimpleName();
        OffsetDateTime now = OffsetDateTime.now();
        boolean isNewObject = false;

        // 1. GENERIC INSTANTIATION MANAGEMENT (if object is null)
        if (targetObject == null) {
            try {
                Constructor<T> constructor = clazz.getConstructor();
                targetObject = constructor.newInstance();
                isNewObject = true;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("BDM class " + objectName + " must have a default (no-argument) constructor.", e);
            } catch (Exception e) {
                // Handles InstantiationException, IllegalAccessException, InvocationTargetException during construction
                throw new RuntimeException("Failed to instantiate BDM object " + objectName + " generically.", e);
            }
        }
        
        try {
            if (isNewObject) {
                invokeSetter(targetObject, "setCreationDate", OffsetDateTime.class, now);
                invokeSetter(targetObject, "setCreatorId", Long.class, initiator.id());
                invokeSetter(targetObject, "setCreatorName", String.class, initiator.fullName());
                LOGGER.warn("No existing {} found. Creating a new record.", objectName);
            } else {
                invokeSetter(targetObject, "setModificationDate", OffsetDateTime.class, now);
                invokeSetter(targetObject, "setModifierId", Long.class, initiator.id());
                invokeSetter(targetObject, "setModifierName", String.class, initiator.fullName());
                LOGGER.warn("Found existing {} with ID {}. Updating record.", objectName, persistenceId);
            }

        } catch (InvocationTargetException e) {
            // Catches exceptions thrown by the invoked method (setter/getter)
            throw new RuntimeException("Error during BDM method call in " + objectName, e.getTargetException());
        } catch (Exception e) {
            // Catches other Reflection exceptions (NoSuchMethodException, IllegalAccessException, etc.)
            LOGGER.error("FATAL: Error applying audit data using Reflection to " + objectName 
                + ". Check method names (getCreationDate, setCreatorId, etc).");
            throw new RuntimeException("BDM audit update failed due to Reflection error.", e);
        }

        return targetObject;
    }
    
    /**
     * Auxiliary private method to call a setter method by its name using Reflection.
     *
     * @param target The object on which to call the method.
     * @param methodName The name of the setter method (e.g., "setCreatorId").
     * @param paramType The class of the single parameter (e.g., Long.class).
     * @param value The value to pass as the argument.
     * @throws Exception Various reflection exceptions (NoSuchMethodException, IllegalAccessException, InvocationTargetException).
     */
    private static void invokeSetter(Object target, String methodName, Class<?> paramType, Object value) throws Exception {
        Method setter = target.getClass().getMethod(methodName, paramType);
        setter.invoke(target, value);
    }
}