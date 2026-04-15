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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
    // Multipart file upload tests
    // ========================================================================

    @Test
    void should_build_multipart_body_when_file_content_provided() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "{\"id\":\"abc\"}", RestContentType.JSON, 100L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://www.googleapis.com",
                    "methods": [{
                        "name": "uploadFile", "httpMethod": "POST",
                        "path": "/upload/drive/v3/files",
                        "queryParams": {"uploadType": "multipart"}
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        String fileBase64 = Base64.getEncoder().encodeToString("fake-pdf-bytes".getBytes(StandardCharsets.UTF_8));

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("uploadFile")
                .body("{\"name\":\"report.pdf\",\"mimeType\":\"application/pdf\"}")
                .fileContentBase64(fileBase64)
                .fileContentType("application/pdf")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        RestServiceRequest captured = captor.getValue();

        // Should have rawBody (multipart) instead of plain text body
        assertThat(captured.hasRawBody()).isTrue();
        assertThat(captured.contentTypeOverride()).startsWith("multipart/related; boundary=");

        // Verify multipart content contains both metadata and file
        String multipartBody = new String(captured.rawBody(), StandardCharsets.UTF_8);
        assertThat(multipartBody).contains("application/json");
        assertThat(multipartBody).contains("\"name\":\"report.pdf\"");
        assertThat(multipartBody).contains("Content-Type: application/pdf");
        assertThat(multipartBody).contains("fake-pdf-bytes");
    }

    @Test
    void should_not_build_multipart_when_no_file_content() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().hasRawBody()).isFalse();
        assertThat(captor.getValue().contentTypeOverride()).isNull();
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
    // placeholderConfig tests
    // ========================================================================

    @Test
    void should_apply_fixed_placeholder_ignoring_pm_value() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "listFiles", "httpMethod": "GET", "path": "/files",
                        "queryParams": {"pageSize": "{{limit}}"},
                        "placeholderConfig": {
                            "limit": {"mode": "FIXED", "value": "100"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("listFiles")
                .params(Map.of("limit", "50"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().queryParams()).containsEntry("pageSize", "100");
    }

    @Test
    void should_apply_default_placeholder_when_pm_provides_value() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "listFiles", "httpMethod": "GET", "path": "/files",
                        "queryParams": {"q": "{{query}}"},
                        "placeholderConfig": {
                            "query": {"mode": "DEFAULT", "value": "trashed=false"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("listFiles")
                .params(Map.of("query", "name='test'"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().queryParams()).containsEntry("q", "name='test'");
    }

    @Test
    void should_apply_default_placeholder_when_pm_provides_nothing() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "listFiles", "httpMethod": "GET", "path": "/files",
                        "queryParams": {"q": "{{query}}"},
                        "placeholderConfig": {
                            "query": {"mode": "DEFAULT", "value": "trashed=false"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("listFiles")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().queryParams()).containsEntry("q", "trashed=false");
    }

    @Test
    void should_keep_dynamic_placeholder_from_pm() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "getFile", "httpMethod": "GET", "path": "/files/{{fileId}}",
                        "placeholderConfig": {
                            "fileId": {"mode": "DYNAMIC"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("getFile")
                .params(Map.of("fileId", "abc123"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/files/abc123");
    }

    @Test
    void should_work_without_placeholder_config() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/v1/users");
    }

    @Test
    void should_resolve_doc_name_placeholder_from_file_name() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://www.googleapis.com",
                    "methods": [{
                        "name": "upload", "httpMethod": "POST",
                        "path": "/upload/drive/v3/files",
                        "queryParams": {"uploadType": "multipart"},
                        "bodyTemplate": "{\\"name\\":\\"{{name}}\\",\\"mimeType\\":\\"{{mimeType}}\\",\\"parents\\":[\\"{{parents}}\\"]}",
                        "placeholderConfig": {
                            "name":     {"mode": "DOC_NAME"},
                            "mimeType": {"mode": "DOC_MIMETYPE"},
                            "parents":  {"mode": "FIXED", "value": "folder123"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        String fileBase64 = Base64.getEncoder().encodeToString("pdf-bytes".getBytes(StandardCharsets.UTF_8));

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("upload")
                .fileContentBase64(fileBase64)
                .fileContentType("application/pdf")
                .fileName("rapport-Q1.pdf")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        RestServiceRequest captured = captor.getValue();

        // Multipart body should contain resolved metadata
        assertThat(captured.hasRawBody()).isTrue();
        String body = new String(captured.rawBody(), StandardCharsets.UTF_8);
        assertThat(body).contains("\"name\":\"rapport-Q1.pdf\"");
        assertThat(body).contains("\"mimeType\":\"application/pdf\"");
        assertThat(body).contains("\"parents\":[\"folder123\"]");
        assertThat(body).doesNotContain("{{name}}");
        assertThat(body).doesNotContain("{{mimeType}}");
    }

    @Test
    void should_resolve_doc_mimetype_without_file_upload() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "search", "httpMethod": "GET", "path": "/files",
                        "queryParams": {"q": "mimeType='{{mimeType}}'"},
                        "placeholderConfig": {
                            "mimeType": {"mode": "DOC_MIMETYPE"}
                        }
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("search")
                .fileContentType("application/pdf")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().queryParams()).containsEntry("q", "mimeType='application/pdf'");
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

    // ========================================================================
    // Test-unsaved-method overrides (pathOverride + REPLACE headers/queryParams)
    // ========================================================================

    @Test
    void should_apply_path_override_instead_of_method_path() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .pathOverride("/v2/admins")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/v2/admins");
    }

    @Test
    void should_substitute_placeholders_in_path_override() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .pathOverride("/v1/users/{{id}}/profile")
                .params(Map.of("id", "42"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().url()).isEqualTo("https://api.example.com/v1/users/42/profile");
    }

    @Test
    void should_replace_method_headers_when_request_headers_non_empty() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "call", "httpMethod": "GET", "path": "/x",
                        "headers": {"X-Method-Only": "from-method", "X-Shared": "method-val"}
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("call")
                .headers(Map.of("X-Override", "from-request", "X-Shared", "request-val"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        Map<String, String> headers = captor.getValue().headers();
        // REPLACE semantics: method's X-Method-Only must NOT be present
        assertThat(headers).doesNotContainKey("X-Method-Only");
        assertThat(headers).containsEntry("X-Override", "from-request");
        assertThat(headers).containsEntry("X-Shared", "request-val");
    }

    @Test
    void should_replace_method_query_params_when_request_query_params_non_empty() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "call", "httpMethod": "GET", "path": "/x",
                        "queryParams": {"page": "1", "size": "10"}
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("call")
                .queryParams(Map.of("filter", "active"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        Map<String, String> qp = captor.getValue().queryParams();
        // REPLACE semantics: method's page/size must NOT be present
        assertThat(qp).doesNotContainKeys("page", "size");
        assertThat(qp).containsEntry("filter", "active");
    }

    @Test
    void should_substitute_placeholders_in_override_headers_and_query_params() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        ConnectorRequest request = ConnectorRequest.builder(NEW_CONFIG)
                .methodName("getUsers")
                .headers(Map.of("X-Token", "Bearer {{token}}"))
                .queryParams(Map.of("q", "user-{{id}}"))
                .params(Map.of("token", "secret-abc", "id", "42"))
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        assertThat(captor.getValue().headers()).containsEntry("X-Token", "Bearer secret-abc");
        assertThat(captor.getValue().queryParams()).containsEntry("q", "user-42");
    }

    @Test
    void should_keep_method_headers_and_query_params_when_no_override() {
        RestServiceResponse httpResponse = RestServiceResponse.success(
                200, Map.of(), "OK", RestContentType.JSON, 50L, "url");
        when(mockHttpExecutor.execute(any(RestServiceRequest.class))).thenReturn(httpResponse);

        String config = """
                {
                    "baseUrl": "https://api.example.com",
                    "methods": [{
                        "name": "call", "httpMethod": "GET", "path": "/x",
                        "headers": {"X-Method": "keep"},
                        "queryParams": {"q": "keep"}
                    }],
                    "auth": {"authType": "none"}
                }
                """;

        ConnectorRequest request = ConnectorRequest.builder(config)
                .methodName("call")
                .build();

        engine.execute(request);

        ArgumentCaptor<RestServiceRequest> captor = ArgumentCaptor.forClass(RestServiceRequest.class);
        verify(mockHttpExecutor).execute(captor.capture());
        // Backward compat: when request has no overrides, method values are preserved
        assertThat(captor.getValue().headers()).containsEntry("X-Method", "keep");
        assertThat(captor.getValue().queryParams()).containsEntry("q", "keep");
    }
}
