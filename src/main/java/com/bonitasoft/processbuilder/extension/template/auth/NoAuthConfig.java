package com.bonitasoft.processbuilder.extension.template.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Configuration for APIs that require no authentication.
 *
 * @author Process Builder Team
 * @since 2025-02-06
 */
public final class NoAuthConfig implements AuthConfig {

    public static final NoAuthConfig INSTANCE = new NoAuthConfig();

    private NoAuthConfig() {}

    @Override
    public String getAuthType() {
        return "none";
    }

    @Override
    public JsonNode toJson(ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("authType", getAuthType());
        return node;
    }

    @Override
    public JsonNode toJsonEncrypted(ObjectMapper mapper) {
        return toJson(mapper);
    }

    @Override
    public String toString() {
        return "NoAuthConfig[]";
    }
}
