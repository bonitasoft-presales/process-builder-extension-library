package com.bonitasoft.processbuilder.execution;

import java.util.Collections;
import java.util.Map;

/**
 * Output response from the ConnectorExecutionEngine.
 *
 * @param success        Whether the execution was successful (HTTP 2xx)
 * @param statusCode     The HTTP status code (-1 if connection error)
 * @param responseBody   The response body as string
 * @param responseHeaders The response headers
 * @param errorMessage   Error description (null if successful)
 * @param executionTimeMs Time taken in milliseconds
 * @param requestUrl     The final URL that was called
 * @param mappedData     Optional transformed data from fieldMapping (null if no mapping)
 */
public record ConnectorResponse(
        boolean success,
        int statusCode,
        String responseBody,
        Map<String, String> responseHeaders,
        String errorMessage,
        long executionTimeMs,
        String requestUrl,
        String mappedData
) {

    public ConnectorResponse {
        responseHeaders = responseHeaders != null ? Map.copyOf(responseHeaders) : Collections.emptyMap();
    }

    public static ConnectorResponse success(int statusCode, String responseBody,
                                             Map<String, String> responseHeaders,
                                             long executionTimeMs, String requestUrl) {
        return new ConnectorResponse(true, statusCode, responseBody, responseHeaders,
                null, executionTimeMs, requestUrl, null);
    }

    public static ConnectorResponse success(int statusCode, String responseBody,
                                             Map<String, String> responseHeaders,
                                             long executionTimeMs, String requestUrl,
                                             String mappedData) {
        return new ConnectorResponse(true, statusCode, responseBody, responseHeaders,
                null, executionTimeMs, requestUrl, mappedData);
    }

    public static ConnectorResponse error(String errorMessage, long executionTimeMs, String requestUrl) {
        return new ConnectorResponse(false, -1, null, Collections.emptyMap(),
                errorMessage, executionTimeMs, requestUrl, null);
    }

    public static ConnectorResponse error(int statusCode, String responseBody, String errorMessage,
                                           long executionTimeMs, String requestUrl) {
        return new ConnectorResponse(false, statusCode, responseBody, Collections.emptyMap(),
                errorMessage, executionTimeMs, requestUrl, null);
    }
}
