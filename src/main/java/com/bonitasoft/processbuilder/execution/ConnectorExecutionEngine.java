package com.bonitasoft.processbuilder.execution;

import com.bonitasoft.processbuilder.enums.RestHttpMethod;
import com.bonitasoft.processbuilder.records.RestAuthConfig;
import com.bonitasoft.processbuilder.records.RestServiceRequest;
import com.bonitasoft.processbuilder.records.RestServiceResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade for executing REST connectors.
 * <p>
 * This is the single entry point used by both consumers:
 * <ul>
 *   <li><b>REST API Extension</b> (ExecuteRestService controller)</li>
 *   <li><b>Custom Bonita Connector</b> (RestExecutionConnector in RestAPIConnector process)</li>
 * </ul>
 * </p>
 * <p>
 * The engine parses the PBConfiguration JSON, resolves the method template,
 * substitutes parameters, normalizes + decrypts authentication, and executes
 * the HTTP call via {@link HttpExecutor}.
 * </p>
 */
public final class ConnectorExecutionEngine {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorExecutionEngine.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpExecutor httpExecutor;

    public ConnectorExecutionEngine() {
        this.httpExecutor = new HttpExecutor();
    }

    public ConnectorExecutionEngine(HttpExecutor httpExecutor) {
        this.httpExecutor = httpExecutor;
    }

    /**
     * Executes a REST connector request.
     *
     * @param request The connector request containing configJson, methodName, params, etc.
     * @return The connector response with success/error, statusCode, responseBody, etc.
     */
    public ConnectorResponse execute(ConnectorRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            LOGGER.info("ConnectorExecutionEngine: executing actionType={}, methodName={}",
                    request.actionType(), request.methodName());

            // 1. Parse the PBConfiguration JSON
            JsonNode configJson = MAPPER.readTree(request.configJson());

            // 2. Detect structure type and build RestServiceRequest
            boolean isNewStructure = configJson.has("baseUrl") && configJson.has("methods");
            boolean isLegacyStructure = configJson.has("url");

            RestServiceRequest.Builder builder;

            if (isNewStructure) {
                builder = buildFromNewStructure(configJson, request);
            } else if (isLegacyStructure) {
                builder = buildFromLegacyStructure(configJson, request);
            } else {
                return ConnectorResponse.error(
                        "Invalid configuration: missing 'baseUrl'+'methods' or 'url'",
                        elapsed(startTime), null);
            }

            // 3. Apply runtime overrides from ConnectorRequest
            applyOverrides(builder, request);

            RestServiceRequest restRequest = builder.build();

            // 4. Execute HTTP call
            RestServiceResponse restResponse = httpExecutor.execute(restRequest);

            // 5. Map to ConnectorResponse
            long executionTime = elapsed(startTime);

            if (restResponse.isSuccessful()) {
                return ConnectorResponse.success(
                        restResponse.statusCode(),
                        restResponse.body(),
                        restResponse.headers(),
                        executionTime,
                        restResponse.url());
            } else {
                String errorMsg = restResponse.errorMessage() != null
                        ? restResponse.errorMessage()
                        : "HTTP " + restResponse.statusCode();
                return ConnectorResponse.error(
                        restResponse.statusCode(),
                        restResponse.body(),
                        errorMsg,
                        executionTime,
                        restResponse.url());
            }

        } catch (Exception e) {
            LOGGER.error("ConnectorExecutionEngine failed: {}", e.getMessage(), e);
            return ConnectorResponse.error(e.getMessage(), elapsed(startTime), null);
        }
    }

    // ========================================================================
    // NEW structure: baseUrl + methods[]
    // ========================================================================

    private RestServiceRequest.Builder buildFromNewStructure(JsonNode configJson, ConnectorRequest request)
            throws Exception {

        String baseUrl = configJson.get("baseUrl").asText();

        // Substitute {{param}} in baseUrl
        Map<String, String> allParams = new HashMap<>(request.params());
        baseUrl = TemplateSubstitution.substitute(baseUrl, allParams);

        // Find method by methodName
        JsonNode methodsArray = configJson.get("methods");
        if (methodsArray == null || !methodsArray.isArray()) {
            throw new IllegalArgumentException("Missing or invalid 'methods' array in configuration");
        }

        String methodName = request.methodName();
        if (methodName.isEmpty()) {
            throw new IllegalArgumentException("methodName is required. Available: " + getMethodNames(methodsArray));
        }

        JsonNode methodConfig = null;
        for (JsonNode method : methodsArray) {
            if (method.has("name") && methodName.equals(method.get("name").asText())) {
                methodConfig = method;
                break;
            }
        }

        if (methodConfig == null) {
            throw new IllegalArgumentException(
                    "Method '" + methodName + "' not found. Available: " + getMethodNames(methodsArray));
        }

        // Extract HTTP method and path
        String httpMethod = methodConfig.has("httpMethod") ? methodConfig.get("httpMethod").asText() : "GET";
        String path = methodConfig.has("path") ? methodConfig.get("path").asText() : "";

        // Substitute {{param}} in path
        path = TemplateSubstitution.substitute(path, allParams);

        // Build final URL
        String finalUrl = TemplateSubstitution.buildFinalUrl(baseUrl, path);
        LOGGER.debug("Built final URL: {}", finalUrl);

        RestServiceRequest.Builder builder = RestServiceRequest.builder(finalUrl);

        // Set HTTP method
        RestHttpMethod.fromKey(httpMethod).ifPresent(builder::method);

        // Apply base configuration (auth, headers, timeout, SSL)
        applyBaseConfig(builder, configJson, allParams);

        // Apply method-specific query parameters
        if (methodConfig.has("queryParams") && methodConfig.get("queryParams").isObject()) {
            methodConfig.get("queryParams").fields().forEachRemaining(entry -> {
                String value = TemplateSubstitution.substitute(entry.getValue().asText(), allParams);
                builder.queryParam(entry.getKey(), value);
            });
        }

        // Apply method-specific headers
        if (methodConfig.has("headers") && methodConfig.get("headers").isObject()) {
            methodConfig.get("headers").fields().forEachRemaining(entry -> {
                String value = TemplateSubstitution.substitute(entry.getValue().asText(), allParams);
                builder.header(entry.getKey(), value);
            });
        }

        // Apply body template
        if (methodConfig.has("bodyTemplate") && !methodConfig.get("bodyTemplate").asText().isEmpty()) {
            String body = TemplateSubstitution.substitute(methodConfig.get("bodyTemplate").asText(), allParams);
            builder.body(body);
        }

        return builder;
    }

    // ========================================================================
    // LEGACY structure: url
    // ========================================================================

    private RestServiceRequest.Builder buildFromLegacyStructure(JsonNode configJson, ConnectorRequest request) {
        String url = configJson.get("url").asText();

        RestServiceRequest.Builder builder = RestServiceRequest.builder(url);

        // Apply method
        if (configJson.has("method")) {
            RestHttpMethod.fromKey(configJson.get("method").asText()).ifPresent(builder::method);
        }

        // Apply headers
        if (configJson.has("headers") && configJson.get("headers").isObject()) {
            Map<String, String> headers = new HashMap<>();
            configJson.get("headers").fields().forEachRemaining(entry ->
                    headers.put(entry.getKey(), entry.getValue().asText()));
            builder.headers(headers);
        }

        // Apply query params from config
        if (configJson.has("queryParams") && configJson.get("queryParams").isObject()) {
            Map<String, String> qp = new HashMap<>();
            configJson.get("queryParams").fields().forEachRemaining(entry ->
                    qp.put(entry.getKey(), entry.getValue().asText()));
            builder.queryParams(qp);
        }

        // Apply timeout
        if (configJson.has("timeoutMs")) {
            builder.timeout(configJson.get("timeoutMs").asInt());
        }

        // Apply SSL
        if (configJson.has("verifySsl")) {
            builder.verifySsl(configJson.get("verifySsl").asBoolean(true));
        }

        // Apply redirects
        if (configJson.has("followRedirects")) {
            builder.followRedirects(configJson.get("followRedirects").asBoolean(true));
        }

        // Apply auth with normalize + decrypt pipeline (FIX for legacy bug)
        if (configJson.has("auth") && configJson.get("auth").isObject()) {
            RestAuthConfig authConfig = AuthPipeline.resolve(configJson.get("auth"));
            builder.auth(authConfig);
        }

        return builder;
    }

    // ========================================================================
    // Common helpers
    // ========================================================================

    private void applyBaseConfig(RestServiceRequest.Builder builder, JsonNode configJson, Map<String, String> params) {
        if (configJson.has("timeoutMs")) {
            builder.timeout(configJson.get("timeoutMs").asInt());
        }
        if (configJson.has("verifySsl")) {
            builder.verifySsl(configJson.get("verifySsl").asBoolean(true));
        }
        if (configJson.has("followRedirects")) {
            builder.followRedirects(configJson.get("followRedirects").asBoolean(true));
        }

        // Apply base headers with template substitution
        if (configJson.has("headers") && configJson.get("headers").isObject()) {
            configJson.get("headers").fields().forEachRemaining(entry -> {
                String value = TemplateSubstitution.substitute(entry.getValue().asText(), params);
                builder.header(entry.getKey(), value);
            });
        }

        // Apply auth with full pipeline (normalize + decrypt)
        if (configJson.has("auth") && configJson.get("auth").isObject()) {
            RestAuthConfig authConfig = AuthPipeline.resolve(configJson.get("auth"));
            builder.auth(authConfig);
        }
    }

    private void applyOverrides(RestServiceRequest.Builder builder, ConnectorRequest request) {
        // Override HTTP method
        if (!request.methodOverride().isEmpty()) {
            RestHttpMethod.fromKey(request.methodOverride()).ifPresent(builder::method);
        }

        // Override body
        if (!request.body().isEmpty()) {
            builder.body(request.body());
        }

        // Override timeout
        if (request.timeoutMs() > 0) {
            builder.timeout(request.timeoutMs());
        }

        // Override SSL
        if (request.verifySsl() != null) {
            builder.verifySsl(request.verifySsl());
        }

        // Add extra headers
        if (!request.headers().isEmpty()) {
            request.headers().forEach(builder::header);
        }

        // Add URL query parameters
        if (!request.queryParams().isEmpty()) {
            request.queryParams().forEach(builder::queryParam);
        }
    }

    private String getMethodNames(JsonNode methodsArray) {
        List<String> names = new ArrayList<>();
        for (JsonNode method : methodsArray) {
            if (method.has("name")) {
                names.add(method.get("name").asText());
            }
        }
        return String.join(", ", names);
    }

    private long elapsed(long startTime) {
        return System.currentTimeMillis() - startTime;
    }
}
