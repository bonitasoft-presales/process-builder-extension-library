/**
 * Provides classes and utilities for extending the Bonita platform's functionality.
 * <p>
 * This package includes utility classes for handling exceptions ({@link com.bonitasoft.processbuilder.extension.ExceptionUtils})
 * and for obtaining process-related information ({@link com.bonitasoft.processbuilder.extension.ProcessUtils}),
 * which are essential for building robust and custom business logic.
 * </p>
 *
 * <h2>Template Variable Resolution</h2>
 * <p>
 * Use {@link com.bonitasoft.processbuilder.extension.TemplateDataResolver} together with
 * {@link com.bonitasoft.processbuilder.extension.PBStringUtils#resolveTemplateVariables}
 * to resolve template variables in notification messages.
 * </p>
 *
 * <h3>Supported Variable Formats:</h3>
 * <ul>
 *   <li>{@code {{dataName}}} - Simple variable (e.g., {{recipient_firstname}}, {{task_link}})</li>
 *   <li>{@code {{refStep:dataName}}} - Variable with step reference (e.g., {{step_123:step_user_name}})</li>
 * </ul>
 *
 * @author Bonitasoft
 * @since 1.0
 */
package com.bonitasoft.processbuilder.extension;
