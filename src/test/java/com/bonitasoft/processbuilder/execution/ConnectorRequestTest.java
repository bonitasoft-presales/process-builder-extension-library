package com.bonitasoft.processbuilder.execution;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectorRequestTest {

    @Test
    void should_create_with_defaults() {
        ConnectorRequest request = ConnectorRequest.builder("{}")
                .build();

        assertThat(request.configJson()).isEqualTo("{}");
        assertThat(request.actionType()).isEmpty();
        assertThat(request.methodName()).isEmpty();
        assertThat(request.params()).isEmpty();
        assertThat(request.body()).isEmpty();
        assertThat(request.headers()).isEmpty();
        assertThat(request.fieldMappingJson()).isEmpty();
        assertThat(request.timeoutMs()).isZero();
        assertThat(request.verifySsl()).isNull();
        assertThat(request.methodOverride()).isEmpty();
        assertThat(request.queryParams()).isEmpty();
        assertThat(request.fileContentBase64()).isEmpty();
        assertThat(request.fileContentType()).isEmpty();
        assertThat(request.fileName()).isEmpty();
    }

    @Test
    void should_build_with_all_params() {
        ConnectorRequest request = ConnectorRequest.builder("{\"url\": \"test\"}")
                .actionType("restApis")
                .methodName("getUsers")
                .params(Map.of("id", "42"))
                .body("{\"key\": \"value\"}")
                .headers(Map.of("Authorization", "Bearer token"))
                .fieldMappingJson("{\"mapping\": true}")
                .timeoutMs(5000)
                .verifySsl(false)
                .methodOverride("POST")
                .queryParams(Map.of("page", "1"))
                .fileContentBase64("dGVzdA==")
                .fileContentType("application/pdf")
                .fileName("report.pdf")
                .build();

        assertThat(request.configJson()).isEqualTo("{\"url\": \"test\"}");
        assertThat(request.actionType()).isEqualTo("restApis");
        assertThat(request.methodName()).isEqualTo("getUsers");
        assertThat(request.params()).containsEntry("id", "42");
        assertThat(request.body()).isEqualTo("{\"key\": \"value\"}");
        assertThat(request.headers()).containsEntry("Authorization", "Bearer token");
        assertThat(request.fieldMappingJson()).isEqualTo("{\"mapping\": true}");
        assertThat(request.timeoutMs()).isEqualTo(5000);
        assertThat(request.verifySsl()).isFalse();
        assertThat(request.methodOverride()).isEqualTo("POST");
        assertThat(request.queryParams()).containsEntry("page", "1");
        assertThat(request.fileContentBase64()).isEqualTo("dGVzdA==");
        assertThat(request.fileContentType()).isEqualTo("application/pdf");
        assertThat(request.fileName()).isEqualTo("report.pdf");
    }

    @Test
    void should_handle_null_configJson() {
        ConnectorRequest request = new ConnectorRequest(
                null, null, null, null, null, null, null, 0, null, null, null, null, null, null);
        assertThat(request.configJson()).isEqualTo("{}");
        assertThat(request.actionType()).isEmpty();
        assertThat(request.methodOverride()).isEmpty();
        assertThat(request.queryParams()).isEmpty();
    }

    @Test
    void should_make_params_immutable() {
        ConnectorRequest request = ConnectorRequest.builder("{}")
                .param("key", "value")
                .build();

        assertThat(request.params()).containsEntry("key", "value");
    }
}
