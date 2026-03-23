package com.bonitasoft.processbuilder.execution;

import com.bonitasoft.processbuilder.records.RestServiceRequest;
import com.bonitasoft.processbuilder.records.RestServiceResponse;
import com.bonitasoft.processbuilder.enums.RestContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectorExecutionEngineTest {

    private static final String NEW_CONFIG = """
            {
                "baseUrl": "https://api.example.com",
                "methods": [
                    {"name": "getUsers", "httpMethod": "GET", "path": "/v1/users"},
                    {"name": "createUser", "httpMethod": "POST", "path": "/v1/users", "bodyTemplate": "{\\"name\\": \\"{{userName}}\\"}"}
                ],
                "auth": {"authType": "bearer", "token": "test-token"},
                "timeoutMs": 5000,
                "verifySsl": true
            }
            """;

    private static final String LEGACY_CONFIG = """
            {
                "url": "https://legacy.example.com/api/users",
                "method": "GET",
                "auth": {"authType": "basic", "username": "admin", "password": "secret"},
                "headers": {"Accept": "application/json"},
                "timeoutMs": 10000,
                "verifySsl": false
            }
            """;

    @Mock
    private HttpExecutor mockHttpExecutor;

    private ConnectorExecutionEngine engine;

    @BeforeEach
    void setUp() {
        engine = new ConnectorExecutionEngine(mockHttpExecutor);
    }

    // ========================================================================
    // NEW structure tests
    // ========================================================================

    @Test
    void should_build_request_from_new_structure() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "{\"users\": []}", RestContentType.JSON, 100L, "https://api.example.com/v1/users");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .actionType("restApis")
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.responseBody()).isEqualTo("{\"users\": []}");

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/v1/users");
    }

    @Test
    void should_substitute_params_in_new_structure() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com/{{version}}",
                    "methods": [
                        {"name": "getUser", "httpMethod": "GET", "path": "/users/{{userId}}"}
                    ],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("getUser")
                .params(Map.of("version", "v2", "userId", "42"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/v2/users/42");
    }

    @Test
    void should_apply_body_template() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                201, Map.of(), "{}", RestContentType.JSON, 80L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("createUser")
                .params(Map.of("userName", "Alice"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().body()).isEqualTo("{\"name\": \"Alice\"}");
    }

    @Test
    void should_return_error_when_method_not_found() {
        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("nonExistent")
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).contains("nonExistent").contains("not found");
        verifyNoInteractions(mockHttpExecutor);
    }

    @Test
    void should_return_error_when_method_name_empty() {
        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("")
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).contains("methodName is required");
        verifyNoInteractions(mockHttpExecutor);
    }

    // ========================================================================
    // LEGACY structure tests
    // ========================================================================

    @Test
    void should_build_request_from_legacy_structure() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "[]", RestContentType.JSON, 200L, "https://legacy.example.com/api/users");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(LEGACY_CONFIG)
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isTrue();
        assertThat(response.statusCode()).isEqualTo(200);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        RestServiceRequest captured = captor.getValue();
        assertThat(captured.url()).isEqualTo("https://legacy.example.com/api/users");
        assertThat(captured.timeoutMs()).isEqualTo(10000);
        assertThat(captured.verifySsl()).isFalse();
    }

    @Test
    void should_apply_auth_pipeline_to_legacy_config() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 100L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(LEGACY_CONFIG).build();
        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        // Auth should have been resolved through AuthPipeline (normalize + decrypt)
        assertThat(captor.getValue().auth()).isNotNull();
    }

    // ========================================================================
    // Invalid config tests
    // ========================================================================

    @Test
    void should_return_error_for_invalid_config() {
        ConnectorRequest request = ConnectorRequest.builder("{}")
                .methodName("test")
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).contains("Invalid configuration");
        verifyNoInteractions(mockHttpExecutor);
    }

    @Test
    void should_return_error_for_malformed_json() {
        ConnectorRequest request = ConnectorRequest.builder("not json")
                .build();

        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).isNotNull();
        verifyNoInteractions(mockHttpExecutor);
    }

    // ========================================================================
    // Override tests
    // ========================================================================

    @Test
    void should_apply_body_override() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .body("{\"override\": true}")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().body()).isEqualTo("{\"override\": true}");
    }

    @Test
    void should_apply_timeout_override() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .timeoutMs(60000)
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().timeoutMs()).isEqualTo(60000);
    }

    @Test
    void should_map_http_error_to_connector_error() {
        RestServiceResponse httpError = RestServiceResponse.error("Connection refused", 100L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpError);

        ConnectorRequest request = ConnectorRequest.builder(LEGACY_CONFIG).build();
        ConnectorResponse response = engine.execute(request);

        assertThat(response.success()).isFalse();
        assertThat(response.errorMessage()).contains("Connection refused");
    }
}
