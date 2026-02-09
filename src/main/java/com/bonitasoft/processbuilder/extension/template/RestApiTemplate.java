package com.bonitasoft.processbuilder.extension.template;

import com.bonitasoft.processbuilder.extension.template.auth.AuthConfig;
import com.bonitasoft.processbuilder.extension.template.auth.NoAuthConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a REST API template configuration.
 * <p>
 * Use this class to create type-safe REST API configurations that can be
 * stored in PBConfiguration. This class supports both:
 * </p>
 * <ul>
 *   <li><b>REST API Configurations</b>: Actual API configurations with real credentials</li>
 *   <li><b>REST API Templates</b>: Template definitions with placeholders ({{baseUrl}}, {{token}}, etc.)</li>
 * </ul>
 * <p>
 * When {@code isTemplate} is true, the template includes additional metadata like
 * {@code templateVersion} and {@code requiredFields} that help the UI render
 * configuration forms for users to fill in placeholders.
 * </p>
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public record RestApiTemplate(
        String name,
        String displayName,
        String description,
        String baseUrl,
        int timeoutMs,
        boolean verifySsl,
        AuthConfig auth,
        Map<String, String> headers,
        List<Method> methods,
        // Template-specific fields
        boolean isTemplate,
        String templateVersion,
        List<String> requiredFields
) {

    /**
     * Represents a method/endpoint in the REST API.
     */
    public record Method(
            String name,
            String displayName,
            String description,
            String httpMethod,
            String path,
            Map<String, String> queryParams,
            Map<String, String> headers,
            String bodyTemplate
    ) {
        public Method {
            if (httpMethod == null || httpMethod.isBlank()) httpMethod = "GET";
            if (path == null) path = "";
            queryParams = queryParams != null ? Map.copyOf(queryParams) : Map.of();
            headers = headers != null ? Map.copyOf(headers) : Map.of();
        }

        public Method(String name, String displayName, String path) {
            this(name, displayName, null, "GET", path, null, null, null);
        }

        public JsonNode toJson(ObjectMapper mapper) {
            ObjectNode node = mapper.createObjectNode();
            node.put("name", name);
            node.put("displayName", displayName != null ? displayName : name);
            if (description != null && !description.isBlank()) node.put("description", description);
            node.put("httpMethod", httpMethod);
            node.put("path", path);
            if (!queryParams.isEmpty()) {
                ObjectNode qp = mapper.createObjectNode();
                queryParams.forEach(qp::put);
                node.set("queryParams", qp);
            }
            if (!headers.isEmpty()) {
                ObjectNode h = mapper.createObjectNode();
                headers.forEach(h::put);
                node.set("headers", h);
            }
            if (bodyTemplate != null && !bodyTemplate.isBlank()) {
                node.put("bodyTemplate", bodyTemplate);
            }
            return node;
        }
    }

    public RestApiTemplate {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(baseUrl, "Base URL cannot be null");
        if (displayName == null || displayName.isBlank()) displayName = name;
        if (timeoutMs <= 0) timeoutMs = 30000;
        if (auth == null) auth = NoAuthConfig.INSTANCE;
        headers = headers != null ? Map.copyOf(headers) : Map.of("Accept", "application/json", "Content-Type", "application/json");
        methods = methods != null ? List.copyOf(methods) : List.of();
        // Template-specific fields: default templateVersion to "2.0" for templates
        if (isTemplate && (templateVersion == null || templateVersion.isBlank())) {
            templateVersion = "2.0";
        }
        requiredFields = requiredFields != null ? List.copyOf(requiredFields) : List.of();
    }

    /**
     * Converts this template to JSON format for storage in PBConfiguration.configValue.
     * <p>
     * When {@code isTemplate} is true, includes template metadata (isTemplate, templateVersion, requiredFields).
     * </p>
     */
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode root = mapper.createObjectNode();
        // Template-specific fields at the top when this is a template definition
        if (isTemplate) {
            root.put("isTemplate", true);
            root.put("templateVersion", templateVersion != null ? templateVersion : "2.0");
        }
        root.put("baseUrl", baseUrl);
        root.put("timeoutMs", timeoutMs);
        root.put("verifySsl", verifySsl);
        if (!headers.isEmpty()) {
            ObjectNode h = mapper.createObjectNode();
            headers.forEach(h::put);
            root.set("headers", h);
        }
        root.set("auth", auth.toJson(mapper));
        // Required fields for templates (tells UI which fields user must fill)
        if (isTemplate && !requiredFields.isEmpty()) {
            ArrayNode rf = mapper.createArrayNode();
            for (String field : requiredFields) rf.add(field);
            root.set("requiredFields", rf);
        }
        if (!methods.isEmpty()) {
            ArrayNode methodsArray = mapper.createArrayNode();
            for (Method method : methods) methodsArray.add(method.toJson(mapper));
            root.set("methods", methodsArray);
        }
        return root;
    }

    /**
     * Converts this template to JSON format with encrypted sensitive fields.
     * <p>
     * When {@code isTemplate} is true, includes template metadata (isTemplate, templateVersion, requiredFields).
     * Note: Templates with placeholders don't need encryption since they don't contain real credentials.
     * </p>
     */
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        ObjectNode root = mapper.createObjectNode();
        // Template-specific fields at the top when this is a template definition
        if (isTemplate) {
            root.put("isTemplate", true);
            root.put("templateVersion", templateVersion != null ? templateVersion : "2.0");
        }
        root.put("baseUrl", baseUrl);
        root.put("timeoutMs", timeoutMs);
        root.put("verifySsl", verifySsl);
        if (!headers.isEmpty()) {
            ObjectNode h = mapper.createObjectNode();
            headers.forEach(h::put);
            root.set("headers", h);
        }
        root.set("auth", auth.toJsonEncrypted(mapper));
        // Required fields for templates
        if (isTemplate && !requiredFields.isEmpty()) {
            ArrayNode rf = mapper.createArrayNode();
            for (String field : requiredFields) rf.add(field);
            root.set("requiredFields", rf);
        }
        if (!methods.isEmpty()) {
            ArrayNode methodsArray = mapper.createArrayNode();
            for (Method method : methods) methodsArray.add(method.toJson(mapper));
            root.set("methods", methodsArray);
        }
        return root;
    }

    /**
     * Converts this template to a JSON string for storage.
     */
    public String toJsonString(ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(toJson(mapper));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize template to JSON", e);
        }
    }

    /**
     * Converts this template to a JSON string with encrypted sensitive fields.
     */
    public String toJsonStringEncrypted(ObjectMapper mapper) {
        try {
            return mapper.writeValueAsString(toJsonEncrypted(mapper));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize template to JSON", e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String displayName;
        private String description;
        private String baseUrl;
        private int timeoutMs = 30000;
        private boolean verifySsl = true;
        private AuthConfig auth = NoAuthConfig.INSTANCE;
        private final Map<String, String> headers = new LinkedHashMap<>();
        private final List<Method> methods = new ArrayList<>();
        // Template-specific fields
        private boolean isTemplate = false;
        private String templateVersion = "2.0";
        private final List<String> requiredFields = new ArrayList<>();

        public Builder name(String name) { this.name = name; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder baseUrl(String baseUrl) { this.baseUrl = baseUrl; return this; }
        public Builder timeoutMs(int timeoutMs) { this.timeoutMs = timeoutMs; return this; }
        public Builder verifySsl(boolean verifySsl) { this.verifySsl = verifySsl; return this; }
        public Builder auth(AuthConfig auth) { this.auth = auth; return this; }
        public Builder header(String name, String value) { this.headers.put(name, value); return this; }
        public Builder headers(Map<String, String> headers) { this.headers.putAll(headers); return this; }
        public Builder addMethod(Method method) { this.methods.add(method); return this; }
        public Builder addMethod(String name, String httpMethod, String path) {
            return addMethod(new Method(name, name, null, httpMethod, path, null, null, null));
        }
        public Builder addMethod(String name, String httpMethod, String path, Map<String, String> queryParams) {
            return addMethod(new Method(name, name, null, httpMethod, path, queryParams, null, null));
        }

        /**
         * Adds a method with full configuration including displayName, description, and body template.
         *
         * @param name         Method identifier
         * @param displayName  Human-readable name
         * @param description  Method description
         * @param httpMethod   HTTP method (GET, POST, PUT, DELETE, PATCH)
         * @param path         Endpoint path (can include placeholders like {{id}})
         * @param queryParams  Query parameters (can include placeholders)
         * @param bodyTemplate Body template (can include placeholders like {{field}})
         * @return this builder
         */
        public Builder addMethod(String name, String displayName, String description,
                                 String httpMethod, String path,
                                 Map<String, String> queryParams, String bodyTemplate) {
            return addMethod(new Method(name, displayName, description, httpMethod, path, queryParams, null, bodyTemplate));
        }

        /**
         * Marks this as a template definition (includes isTemplate, templateVersion, requiredFields in JSON).
         *
         * @return this builder
         */
        public Builder asTemplate() {
            this.isTemplate = true;
            return this;
        }

        /**
         * Sets the template version (default: "2.0").
         *
         * @param version Template version string
         * @return this builder
         */
        public Builder templateVersion(String version) {
            this.templateVersion = version;
            return this;
        }

        /**
         * Adds a required field that users must fill when using this template.
         *
         * @param fieldName Name of the required field (e.g., "baseUrl", "token", "username")
         * @return this builder
         */
        public Builder requiredField(String fieldName) {
            this.requiredFields.add(fieldName);
            return this;
        }

        /**
         * Adds multiple required fields.
         *
         * @param fields List of required field names
         * @return this builder
         */
        public Builder requiredFields(List<String> fields) {
            this.requiredFields.addAll(fields);
            return this;
        }

        /**
         * Adds multiple required fields (varargs version).
         *
         * @param fields Required field names
         * @return this builder
         */
        public Builder requiredFields(String... fields) {
            for (String field : fields) this.requiredFields.add(field);
            return this;
        }

        public RestApiTemplate build() {
            if (headers.isEmpty()) {
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
            }
            return new RestApiTemplate(name, displayName, description, baseUrl, timeoutMs, verifySsl, auth,
                    Collections.unmodifiableMap(new LinkedHashMap<>(headers)),
                    Collections.unmodifiableList(new ArrayList<>(methods)),
                    isTemplate, templateVersion,
                    Collections.unmodifiableList(new ArrayList<>(requiredFields)));
        }
    }
}
