package com.bonitasoft.processbuilder.execution;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Input request for the ConnectorExecutionEngine.
 * <p>
 * Encapsulates all the information needed to execute a REST call:
 * the PBConfiguration JSON, the method to invoke, and runtime overrides.
 * </p>
 *
 * @param configJson       The full PBConfiguration.configValue JSON (contains baseUrl, methods[], auth, etc.)
 * @param actionType       The FlowActionType key ("restApis", "notifications")
 * @param methodName       The method name to invoke from the methods[] array (NEW structure)
 * @param params           Runtime parameters for {{param}} substitution (query + path combined)
 * @param body             Optional request body override
 * @param headers          Optional additional headers
 * @param fieldMappingJson Optional field mapping JSON for response transformation
 * @param timeoutMs        Optional timeout override (0 = use config default)
 * @param verifySsl        Optional SSL verification override (null = use config default)
 * @param methodOverride      Optional HTTP method override (e.g., "GET", "POST")
 * @param queryParams         Optional URL query parameters (appended to URL, distinct from template params)
 * @param fileContentBase64   Optional Base64-encoded file content for upload
 * @param fileContentType     Optional MIME type of the file being uploaded
 * @param fileName            Optional original file name (used by DOC_NAME placeholderConfig mode)
 */
public record ConnectorRequest(
        String configJson,
        String actionType,
        String methodName,
        Map<String, String> params,
        String body,
        Map<String, String> headers,
        String fieldMappingJson,
        int timeoutMs,
        Boolean verifySsl,
        String methodOverride,
        Map<String, String> queryParams,
        String fileContentBase64,
        String fileContentType,
        String fileName
) {

    public ConnectorRequest {
        configJson = configJson != null ? configJson : "{}";
        actionType = actionType != null ? actionType : "";
        methodName = methodName != null ? methodName : "";
        params = params != null ? Map.copyOf(params) : Collections.emptyMap();
        body = body != null ? body : "";
        headers = headers != null ? Map.copyOf(headers) : Collections.emptyMap();
        fieldMappingJson = fieldMappingJson != null ? fieldMappingJson : "";
        methodOverride = methodOverride != null ? methodOverride : "";
        queryParams = queryParams != null ? Map.copyOf(queryParams) : Collections.emptyMap();
        fileContentBase64 = fileContentBase64 != null ? fileContentBase64 : "";
        fileContentType = fileContentType != null ? fileContentType : "";
        fileName = fileName != null ? fileName : "";
    }

    public static Builder builder(String configJson) {
        return new Builder(configJson);
    }

    public static class Builder {
        private final String configJson;
        private String actionType = "";
        private String methodName = "";
        private Map<String, String> params = new HashMap<>();
        private String body = "";
        private Map<String, String> headers = new HashMap<>();
        private String fieldMappingJson = "";
        private int timeoutMs = 0;
        private Boolean verifySsl = null;
        private String methodOverride = "";
        private Map<String, String> queryParams = new HashMap<>();
        private String fileContentBase64 = "";
        private String fileContentType = "";
        private String fileName = "";

        private Builder(String configJson) {
            this.configJson = configJson;
        }

        public Builder actionType(String actionType) { this.actionType = actionType; return this; }
        public Builder methodName(String methodName) { this.methodName = methodName; return this; }
        public Builder params(Map<String, String> params) { this.params.putAll(params); return this; }
        public Builder param(String key, String value) { this.params.put(key, value); return this; }
        public Builder body(String body) { this.body = body; return this; }
        public Builder headers(Map<String, String> headers) { this.headers.putAll(headers); return this; }
        public Builder header(String key, String value) { this.headers.put(key, value); return this; }
        public Builder fieldMappingJson(String fieldMappingJson) { this.fieldMappingJson = fieldMappingJson; return this; }
        public Builder timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }
        public Builder verifySsl(Boolean verifySsl) { this.verifySsl = verifySsl; return this; }
        public Builder methodOverride(String methodOverride) { this.methodOverride = methodOverride; return this; }
        public Builder queryParams(Map<String, String> queryParams) { this.queryParams.putAll(queryParams); return this; }
        public Builder queryParam(String key, String value) { this.queryParams.put(key, value); return this; }
        public Builder fileContentBase64(String fileContentBase64) { this.fileContentBase64 = fileContentBase64; return this; }
        public Builder fileContentType(String fileContentType) { this.fileContentType = fileContentType; return this; }
        public Builder fileName(String fileName) { this.fileName = fileName; return this; }

        public ConnectorRequest build() {
            return new ConnectorRequest(configJson, actionType, methodName, params, body, headers,
                    fieldMappingJson, timeoutMs, verifySsl, methodOverride, queryParams,
                    fileContentBase64, fileContentType, fileName);
        }
    }
}
