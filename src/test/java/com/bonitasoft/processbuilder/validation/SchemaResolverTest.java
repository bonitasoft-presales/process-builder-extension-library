package com.bonitasoft.processbuilder.validation;

import com.bonitasoft.processbuilder.constants.SchemaConstants;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.main.JsonSchema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SchemaResolver utility class.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class SchemaResolverTest {

    private static final String RESOURCE_PATH = SchemaConstants.OPENAPI_RESOURCE_PATH;
    private static final String TARGET_SCHEMA = "Category";
    private static final String DUMMY_JSON_INPUT = "{}";
    private static final String BASE_SCHEMA_TITLE = "Base Persistence Schema";
    private static final String GENERIC_PROCESSING_FAILED_MESSAGE = "Schema processing failed.";
    
    // Mensajes exactos del SchemaResolver.java
    private static final String MESSAGE_PARSE_NULL = "OpenAPI file failed to parse or could not be found.";
    private static final String MESSAGE_MISSING_COMPONENTS = "OpenAPI document loaded, but schema components are missing.";
    private static final String MESSAGE_IO_ERROR = "File I/O error during schema loading.";
    
    // Mocks for OpenAPIV3Parser dependencies
    @Mock private SwaggerParseResult parseResult;
    @Mock private OpenAPI openAPI;
    @Mock private Components components;
    @Mock private Map<String, Schema> schemas;
    @Mock private Schema targetSchema;
    @Mock private JsonSchema jsonSchemaValidator; // Mock for the fge JsonSchema object

    // Mocks for schemas used in allOf tests (kept for configuring getValidatorSchema)
    @Mock private Schema refSchemaBase;
    @Mock private Schema refSchemaCategory;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this); 
        
        // Default happy path stubs
        when(parseResult.getOpenAPI()).thenReturn(openAPI);
        when(openAPI.getComponents()).thenReturn(components);
        when(components.getSchemas()).thenReturn(schemas);
        when(schemas.get(TARGET_SCHEMA)).thenReturn(targetSchema);
        
        // Default stubs for a simple targetSchema (no allOf)
        when(targetSchema.getAllOf()).thenReturn(null); 
        when(targetSchema.getProperties()).thenReturn(null);
    }

    // =========================================================================
    // SECTION 1: getValidatorSchema TESTS (Success & Failure Paths)
    // =========================================================================

    @Test
    void getValidatorSchema_should_load_and_resolve_successfully() throws Exception {
        
        when(targetSchema.getTitle()).thenReturn(TARGET_SCHEMA);

        // Setup for componentTitles map creation (to cover private method execution path)
        when(refSchemaBase.getTitle()).thenReturn(BASE_SCHEMA_TITLE);
        when(refSchemaBase.get$ref()).thenReturn(SchemaConstants.SCHEMA_COMPONENTS_PREFIX + "ObjectInputBaseSchema");
        
        when(refSchemaCategory.getTitle()).thenReturn(null); 
        when(refSchemaCategory.get$ref()).thenReturn(SchemaConstants.SCHEMA_COMPONENTS_PREFIX + "CategorySpecificProperties");

        List<Schema> allOfList = List.of(refSchemaBase, refSchemaCategory);
        when(targetSchema.getAllOf()).thenReturn(allOfList);
        
        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            try (MockedStatic<SchemaResolver> resolverMock = mockStatic(SchemaResolver.class, CALLS_REAL_METHODS)) {
                
                // Stub the internal serialization and fge setup step
                resolverMock.when(() -> SchemaResolver.parseJson(anyString()))
                            .thenReturn(JsonNodeFactory.instance.objectNode());
                
                // ACT
                LoadedSchema loadedData = SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
                
                // ASSERT
                assertNotNull(loadedData, "LoadedSchema object must not be null.");
                assertNotNull(loadedData.validator(), "JsonSchema validator must be successfully created.");
                assertEquals(2, loadedData.titles().size()); 
                
                // VERIFY
                verify(mockedParser.constructed().get(0), times(1)).readLocation(eq(RESOURCE_PATH), any(), any());
            }
        }
    }

    /**
     * Tests the failure path where OpenAPI object is null, but messages list is also null.
     * (Cubre la línea 90: if (result.getMessages() != null && !result.getMessages().isEmpty()) es FALSO)
     */
    @Test
    @DisplayName("getValidatorSchema should throw exception when openAPI is null (with no parsing messages)")
    void getValidatorSchema_should_throw_exception_when_openAPI_is_null_no_messages() throws Exception {
        when(parseResult.getOpenAPI()).thenReturn(null);
        // Messages is null, forcing the first 'if' check in the failure block to pass the condition check
        when(parseResult.getMessages()).thenReturn(null); 

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            assertEquals(MESSAGE_PARSE_NULL, thrown.getMessage(), 
                "The exception message must exactly match the parser failure message.");
        }
    }

    @Test
    void getValidatorSchema_should_throw_exception_when_openAPI_is_null() throws Exception {
        when(parseResult.getOpenAPI()).thenReturn(null);
        when(parseResult.getMessages()).thenReturn(List.of("Error: File not found in path"));

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            assertEquals(MESSAGE_PARSE_NULL, thrown.getMessage(), 
                "The exception message must exactly match the parser failure message.");
        }
    }

    @Test
    void getValidatorSchema_should_throw_exception_when_schemas_are_missing() throws Exception {
        when(openAPI.getComponents()).thenReturn(null); 

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            assertEquals(MESSAGE_MISSING_COMPONENTS, thrown.getMessage(),
                "The exception message must exactly match the missing components message.");
        }
    }

    @Test
    void getValidatorSchema_should_throw_exception_when_target_schema_not_found() throws Exception {
        when(schemas.get(TARGET_SCHEMA)).thenReturn(null); 

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            assertEquals(GENERIC_PROCESSING_FAILED_MESSAGE, thrown.getMessage(), 
                "The exception should be caught by the generic catch block and re-wrapped.");
        }
    }

    @Test
    void getValidatorSchema_should_throw_runtime_exception_on_io_exception() throws Exception {
        final IOException ioCause = new IOException("Simulated I/O Error");
        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenThrow(new RuntimeException(ioCause)))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            
            assertEquals(MESSAGE_IO_ERROR, thrown.getMessage(), "El mensaje debe coincidir con el error de I/O.");
            
            assertEquals(ioCause, thrown.getCause().getCause(), "La causa raíz debe ser la IOException simulada.");
            
        }
    }

    @Test
    void getValidatorSchema_should_throw_runtime_exception_on_processing_failure() throws Exception {
        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
             try (MockedStatic<SchemaResolver> resolverMock = mockStatic(SchemaResolver.class, CALLS_REAL_METHODS)) {
                
                // Stub the internal serialization to throw a generic runtime exception
                resolverMock.when(() -> SchemaResolver.parseJson(anyString()))
                            .thenThrow(new IllegalStateException("Simulated processing error"));
                
                RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                    SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
                });
                // Expect the final re-wrapped message
                assertEquals(GENERIC_PROCESSING_FAILED_MESSAGE, thrown.getMessage(), 
                    "The exception message should match the outer processing catch block.");
                assertTrue(thrown.getCause() instanceof IllegalStateException, 
                    "The cause of the exception should be the original internal error.");
            }
        }
    }

    @Test
    @DisplayName("getValidatorSchema should throw exception on generic parsing failure")
    void getValidatorSchema_should_throw_exception_on_generic_parsing_failure() throws Exception {
        // Given: Force the parser to throw a non-IO-related Exception, hitting the second catch block.
        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class,
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenThrow(new IllegalStateException("Parsing library crashed")))) 
        {
            // ACT & ASSERT
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            
            assertEquals("OpenAPI parsing failed unexpectedly.", thrown.getMessage(),
                "The exception message must match the generic parsing failure message.");
        }
    }

    // =========================================================================
    // SECTION 2: parseJson TESTS (Success & Failure)
    // =========================================================================

    /**
     * Tests successful JSON parsing of a valid string.
     */
    @Test
    void parseJson_should_parse_valid_json_successfully() {
        String validJson = "{\"key\": \"value\", \"number\": 123}";
        
        JsonNode resultNode = assertDoesNotThrow(() -> SchemaResolver.parseJson(validJson));
        
        assertTrue(resultNode.isObject());
        assertEquals("value", resultNode.get("key").asText());
    }

    /**
     * Tests the failure path when a malformed JSON string is passed.
     */
    @Test
    void parseJson_should_throw_exception_on_malformed_json() {
        String malformedJson = "{key: 'value'}"; 
        
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            SchemaResolver.parseJson(malformedJson);
        });
        // This exception is thrown *before* the main catch block, so the message is correct.
        assertTrue(thrown.getMessage().startsWith("Error parsing JSON string."),
            "The exception message should start with 'Error parsing JSON string.'.");
    }

    // =========================================================================
    // SECTION 3: Constructor Coverage
    // =========================================================================

    /**
     * Ensures the private constructor throws the required exception to prevent instantiation.
     */
    @Test
    @DisplayName("Constructor should throw UnsupportedOperationException")
    void constructor_should_throw_unsupported_operation_exception() throws Exception {
        
        Constructor<SchemaResolver> constructor = SchemaResolver.class.getDeclaredConstructor();
        
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        
        constructor.setAccessible(true);
        InvocationTargetException thrownException = assertThrows(InvocationTargetException.class, constructor::newInstance);
        
        assertTrue(thrownException.getCause() instanceof UnsupportedOperationException);
    }
    
    // =========================================================================
    // SECTION 4: isJsonValid (Simplified coverage, requires mocked fge)
    // =========================================================================
    
    @Test
    void isJsonValid_should_return_true_on_success() throws Exception {
        
        // Create a mock ProcessingReport and chain the validation result correctly.
        com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
        when(mockReport.isSuccess()).thenReturn(true);

        try (MockedStatic<SchemaResolver> resolverMock = mockStatic(SchemaResolver.class, CALLS_REAL_METHODS)) {
            
            // Mock dependencies: jsonSchemaValidator.validate() -> mockReport
            when(jsonSchemaValidator.validate(any(JsonNode.class))).thenReturn(mockReport);
            
            resolverMock.when(() -> SchemaResolver.parseJson(anyString()))
                        .thenReturn(JsonNodeFactory.instance.objectNode());
            
            LoadedSchema loadedSchema = new LoadedSchema(jsonSchemaValidator, Collections.emptyMap(), TARGET_SCHEMA, DUMMY_JSON_INPUT);
            
            // ACT & ASSERT
            assertTrue(SchemaResolver.isJsonValid(loadedSchema));
        }
    }

    @Test
    void isJsonValid_should_return_false_and_log_on_validation_failure() throws Exception {
        try (MockedStatic<SchemaResolver> resolverMock = mockStatic(SchemaResolver.class, CALLS_REAL_METHODS)) {
            com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
            when(jsonSchemaValidator.validate(any(JsonNode.class))).thenReturn(mockReport);
            when(mockReport.isSuccess()).thenReturn(false);
            
            // Fix type safety for spliterator
            Spliterator<ProcessingMessage> emptyMessageSpliterator = Spliterators.spliterator(Collections.emptyList(), 0);
            when(mockReport.spliterator()).thenReturn(emptyMessageSpliterator);
            
            // Mock internal calls
            resolverMock.when(() -> SchemaResolver.parseJson(anyString()))
                        .thenReturn(JsonNodeFactory.instance.objectNode());
            resolverMock.when(() -> SchemaResolver.printRelevantValidationErrors(eq(mockReport), any())).then(invocation -> null);
            
            LoadedSchema loadedSchema = new LoadedSchema(jsonSchemaValidator, Collections.emptyMap(), TARGET_SCHEMA, DUMMY_JSON_INPUT);

            // ACT & ASSERT
            assertFalse(SchemaResolver.isJsonValid(loadedSchema));
            
            // VERIFY the error logger was called
            resolverMock.verify(() -> SchemaResolver.printRelevantValidationErrors(eq(mockReport), any()), times(1));
        }
    }

    @Test
    void isJsonValid_should_handle_fatal_processing_error() throws Exception {
        try (MockedStatic<SchemaResolver> resolverMock = mockStatic(SchemaResolver.class, CALLS_REAL_METHODS)) {
            LoadedSchema loadedSchema = new LoadedSchema(jsonSchemaValidator, Collections.emptyMap(), TARGET_SCHEMA, DUMMY_JSON_INPUT);

            // Mock parseJson to throw an exception, triggering the final catch block
            resolverMock.when(() -> SchemaResolver.parseJson(anyString()))
                        .thenThrow(new RuntimeException("Fatal JSON input error"));
            
            // ACT & ASSERT
            assertFalse(SchemaResolver.isJsonValid(loadedSchema));
        }
    }
    
    // =========================================================================
    // SECTION 5: printRelevantValidationErrors TESTS (Complex Coverage)
    // =========================================================================
    
    /**
     * CUBRE: La rama 'else' final del 'if (allOf.equals(keyword) && errorJson.has("reports"))' (errores genéricos)
     */
    @Test
    void printRelevantValidationErrors_should_handle_generic_error() {
        
        com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
        ProcessingMessage mockMessage = mock(ProcessingMessage.class);
        
        ObjectNode errorJson = JsonNodeFactory.instance.objectNode();
        errorJson.put("keyword", "maxLength"); // No es "allOf"
        errorJson.put("message", "String too long");
        
        List<ProcessingMessage> messages = Collections.singletonList(mockMessage);
        Spliterator<ProcessingMessage> messageSpliterator = Spliterators.spliterator(messages, Spliterator.ORDERED);
        when(mockReport.spliterator()).thenReturn(messageSpliterator);
        
        when(mockMessage.getLogLevel()).thenReturn(com.github.fge.jsonschema.core.report.LogLevel.ERROR);
        when(mockMessage.asJson()).thenReturn(errorJson);
        when(mockMessage.getMessage()).thenReturn("String too long");

        // ACT
        SchemaResolver.printRelevantValidationErrors(mockReport, Collections.emptyMap());
    }
    
    /**
     * CUBRE: La rama principal 'if (allOf.equals(keyword) && errorJson.has("reports"))' (Línea 47)
     */
    @Test
    void printRelevantValidationErrors_should_handle_required_property_error() {
        
        com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
        ProcessingMessage mockMessage = mock(ProcessingMessage.class);
        
        // Build the nested JSON structure for a specific 'required' failure within 'allOf'
        ObjectNode allOfError = JsonNodeFactory.instance.objectNode();
        allOfError.put("keyword", "allOf");
        allOfError.put("reports", JsonNodeFactory.instance.objectNode()); // Debe tener reports para no caer en el else
        
        ObjectNode requiredError = JsonNodeFactory.instance.objectNode();
        requiredError.put("keyword", "required");
        requiredError.put("level", "error");
        requiredError.put("message", "object has missing required properties");
        requiredError.set("missing", JsonNodeFactory.instance.arrayNode().add("persistenceId_string"));
        requiredError.set("schema", JsonNodeFactory.instance.objectNode().put("pointer", "/allOf/0"));
        
        ObjectNode reportsNode = JsonNodeFactory.instance.objectNode();
        // CUBRE la rama if (reportNode.isArray())
        reportsNode.set("/allOf/0", JsonNodeFactory.instance.arrayNode().add(requiredError)); 
        allOfError.set("reports", reportsNode);
        
        List<ProcessingMessage> messages = Collections.singletonList(mockMessage);
        Spliterator<ProcessingMessage> messageSpliterator = Spliterators.spliterator(messages, Spliterator.ORDERED);
        when(mockReport.spliterator()).thenReturn(messageSpliterator);
        
        when(mockMessage.getLogLevel()).thenReturn(com.github.fge.jsonschema.core.report.LogLevel.ERROR);
        when(mockMessage.asJson()).thenReturn(allOfError);
        
        // ACT
        Map<String, String> titles = Map.of("/allOf/0", BASE_SCHEMA_TITLE);
        SchemaResolver.printRelevantValidationErrors(mockReport, titles);
    }
    
    // =========================================================================
    // SECTION 6: createComponentTitleMap COVERAGE (Reflection Tests)
    // =========================================================================

    @Test
    @DisplayName("createComponentTitleMap should return empty map when allOf is null")
    void createComponentTitleMap_should_return_empty_map_when_allOf_is_null() throws Exception {
        // Given: targetSchema.getAllOf() returns null by default (from setup).
        
        // 1. Get the private method using Reflection
        Method method = SchemaResolver.class.getDeclaredMethod("createComponentTitleMap", Schema.class);
        method.setAccessible(true);
        
        // 2. ACT: Invoke the static method with targetSchema (which returns null for allOf)
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) method.invoke(null, targetSchema); 

        // ASSERT: Should be an empty map.
        assertTrue(result.isEmpty(), "Result map must be empty when allOf list is null.");
    }
    
    @Test
    @DisplayName("createComponentTitleMap should use Schema title if present")
    void createComponentTitleMap_should_use_schema_title() throws Exception {
        
        final String expectedTitle = "MyCustomTitle";

        // Given: Schema with a title (Cubre 'title != null')
        Schema<?> titledSchema = mock(Schema.class);
        when(titledSchema.getTitle()).thenReturn(expectedTitle);
        when(titledSchema.get$ref()).thenReturn(SchemaConstants.SCHEMA_COMPONENTS_PREFIX + "IgnoredRef");

        List<Schema> allOfList = List.of(titledSchema);
        when(targetSchema.getAllOf()).thenReturn(allOfList);
        
        // 1. Get the private method using Reflection
        Method method = SchemaResolver.class.getDeclaredMethod("createComponentTitleMap", Schema.class);
        method.setAccessible(true);
        
        // 2. ACT: Invoke the static method
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) method.invoke(null, targetSchema); 

        // ASSERT
        assertEquals(1, result.size(), "Result map size must be 1.");
        assertEquals(expectedTitle, result.get("/allOf/0"),
            "Should use the schema's 'title' property.");
    }

    @Test
    @DisplayName("createComponentTitleMap should use default title if ref is invalid")
    void createComponentTitleMap_should_use_default_title_on_missing_ref() throws Exception {
        
        // Given: Schema with no title and an invalid $ref prefix (Cubre la rama 'else' final del ternario)
        Schema<?> missingRefSchema = mock(Schema.class);
        when(missingRefSchema.getTitle()).thenReturn(null);
        when(missingRefSchema.get$ref()).thenReturn("/invalid/ref/path"); 

        List<Schema> allOfList = List.of(missingRefSchema);
        when(targetSchema.getAllOf()).thenReturn(allOfList);
        
        // 1. Get the private method using Reflection
        Method method = SchemaResolver.class.getDeclaredMethod("createComponentTitleMap", Schema.class);
        method.setAccessible(true);
        
        // 2. ACT: Invoke the static method
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) method.invoke(null, targetSchema); 

        // ASSERT
        assertEquals(1, result.size(), "Result map size must be 1.");
        assertEquals("Inline Schema Component", result.get("/allOf/0"),
            "Should default to 'Inline Schema Component' when ref is invalid or missing.");
    }
    
    @Test
    @DisplayName("createComponentTitleMap should use substring for valid ref without title")
    void createComponentTitleMap_should_use_substring_on_valid_ref() throws Exception {
        
        final String refSegment = "MyObjectReference";

        // Given: Schema with no title and a valid $ref prefix (Cubre la rama 'refString != null && refString.startsWith(...)')
        Schema<?> validRefSchema = mock(Schema.class);
        when(validRefSchema.getTitle()).thenReturn(null);
        when(validRefSchema.get$ref()).thenReturn(SchemaConstants.SCHEMA_COMPONENTS_PREFIX + refSegment); 

        List<Schema> allOfList = List.of(validRefSchema);
        when(targetSchema.getAllOf()).thenReturn(allOfList);
        
        // 1. Get the private method using Reflection
        Method method = SchemaResolver.class.getDeclaredMethod("createComponentTitleMap", Schema.class);
        method.setAccessible(true);
        
        // 2. ACT: Invoke the static method
        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) method.invoke(null, targetSchema); 

        // ASSERT: Debe usar el segmento después del prefijo
        assertEquals(1, result.size(), "Result map size must be 1.");
        assertEquals(refSegment, result.get("/allOf/0"),
            "Should use the substring segment from the valid reference string.");
    }
}