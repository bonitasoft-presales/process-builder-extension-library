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
    
    // Exact messages from SchemaResolver.java for assertion consistency
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

    /**
     * Tests the successful flow of loading, resolving, serializing, and returning a LoadedSchema.
     */
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
                // Implicitly verifies that createComponentTitleMap was successfully executed 
                assertEquals(2, loadedData.titles().size()); 
                
                // VERIFY
                verify(mockedParser.constructed().get(0), times(1)).readLocation(eq(RESOURCE_PATH), any(), any());
            }
        }
    }

    /**
     * Tests the critical failure path when the OpenAPI file cannot be parsed (openAPI == null).
     */
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
            // FIX: Use exact string comparison (assertEquals)
            assertEquals(MESSAGE_PARSE_NULL, thrown.getMessage(), 
                "The exception message must exactly match the parser failure message.");
        }
    }

    /**
     * Tests the failure path when components or schemas section is missing from the loaded OpenAPI object.
     */
    @Test
    void getValidatorSchema_should_throw_exception_when_schemas_are_missing() throws Exception {
        when(openAPI.getComponents()).thenReturn(null); 

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            // FIX: Use exact string comparison (assertEquals)
            assertEquals(MESSAGE_MISSING_COMPONENTS, thrown.getMessage(),
                "The exception message must exactly match the missing components message.");
        }
    }

    /**
     * Tests the failure path when the target schema name is not found in the components map.
     */
    @Test
    void getValidatorSchema_should_throw_exception_when_target_schema_not_found() throws Exception {
        when(schemas.get(TARGET_SCHEMA)).thenReturn(null); 

        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenReturn(parseResult))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            
            // This case hits the final generic catch block in SchemaResolver.java
            assertEquals(GENERIC_PROCESSING_FAILED_MESSAGE, thrown.getMessage(), 
                "The exception should be caught by the generic catch block and re-wrapped.");
        }
    }

    /**
     * Tests the failure path when an IOException (I/O error, file system error, or deep Jackson failure) occurs.
     */
    @Test
    void getValidatorSchema_should_throw_runtime_exception_on_io_exception() throws Exception {
        try (MockedConstruction<OpenAPIV3Parser> mockedParser = mockConstruction(OpenAPIV3Parser.class, 
             (mock, context) -> when(mock.readLocation(anyString(), any(), any())).thenThrow(new IOException("Simulated I/O Error")))) 
        {
            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                SchemaResolver.getValidatorSchema(RESOURCE_PATH, TARGET_SCHEMA, DUMMY_JSON_INPUT);
            });
            // FIX: Use exact string comparison (assertEquals)
            assertEquals(MESSAGE_IO_ERROR, thrown.getMessage(),
                "The exception message must exactly match the outer RuntimeException catch block.");
            assertTrue(thrown.getCause() instanceof IOException, 
                "The cause of the exception should be the original IOException.");
        }
    }

    /**
     * Tests the failure path when an unexpected Exception occurs during processing (e.g., deep internal serialization issue).
     */
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
    
    @Test
    void printRelevantValidationErrors_should_handle_generic_error() {
        
        com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
        ProcessingMessage mockMessage = mock(ProcessingMessage.class);
        
        ObjectNode errorJson = JsonNodeFactory.instance.objectNode();
        errorJson.put("keyword", "maxLength");
        errorJson.put("message", "String too long");
        
        // Fix type safety for spliterator
        List<ProcessingMessage> messages = Collections.singletonList(mockMessage);
        Spliterator<ProcessingMessage> messageSpliterator = Spliterators.spliterator(messages, Spliterator.ORDERED);
        when(mockReport.spliterator()).thenReturn(messageSpliterator);
        
        when(mockMessage.getLogLevel()).thenReturn(com.github.fge.jsonschema.core.report.LogLevel.ERROR);
        when(mockMessage.asJson()).thenReturn(errorJson);
        when(mockMessage.getMessage()).thenReturn("String too long");

        // ACT
        SchemaResolver.printRelevantValidationErrors(mockReport, Collections.emptyMap());
    }
    
    @Test
    void printRelevantValidationErrors_should_handle_required_property_error() {
        
        com.github.fge.jsonschema.core.report.ProcessingReport mockReport = mock(com.github.fge.jsonschema.core.report.ProcessingReport.class);
        ProcessingMessage mockMessage = mock(ProcessingMessage.class);
        
        // Build the nested JSON structure for a specific 'required' failure within 'allOf'
        ObjectNode allOfError = JsonNodeFactory.instance.objectNode();
        allOfError.put("keyword", "allOf");
        
        ObjectNode requiredError = JsonNodeFactory.instance.objectNode();
        requiredError.put("keyword", "required");
        requiredError.put("level", "error");
        requiredError.put("message", "object has missing required properties");
        requiredError.set("missing", JsonNodeFactory.instance.arrayNode().add("persistenceId_string"));
        requiredError.set("schema", JsonNodeFactory.instance.objectNode().put("pointer", "/allOf/0"));
        
        ObjectNode reportsNode = JsonNodeFactory.instance.objectNode();
        reportsNode.set("/allOf/0", JsonNodeFactory.instance.arrayNode().add(requiredError));
        allOfError.set("reports", reportsNode);
        
        // Fix type safety for spliterator
        List<ProcessingMessage> messages = Collections.singletonList(mockMessage);
        Spliterator<ProcessingMessage> messageSpliterator = Spliterators.spliterator(messages, Spliterator.ORDERED);
        when(mockReport.spliterator()).thenReturn(messageSpliterator);
        
        when(mockMessage.getLogLevel()).thenReturn(com.github.fge.jsonschema.core.report.LogLevel.ERROR);
        when(mockMessage.asJson()).thenReturn(allOfError);
        
        // ACT
        Map<String, String> titles = Map.of("/allOf/0", BASE_SCHEMA_TITLE);
        SchemaResolver.printRelevantValidationErrors(mockReport, titles);
    }
}