package com.bonitasoft.processbuilder.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bonitasoft.processbuilder.constants.SchemaConstants;
import com.bonitasoft.processbuilder.records.LoadedSchema;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map.Entry;

public class SchemaResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaResolver.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    // NOTE: Changed to getJsonSchema to adhere to user's specific request
    private static final JsonSchemaFactory SCHEMA_FACTORY = JsonSchemaFactory.byDefault();

    /**
     * Private constructor to prevent instantiation of this utility class.
     * All methods in this class are static and should be called directly on the class itself.
     */
    private SchemaResolver() {
        throw new UnsupportedOperationException("This is a "+this.getClass().getSimpleName()+" class and cannot be instantiated.");
    }


    /**
     * Loads the OpenAPI document, resolves dependencies, and prepares the JsonSchema validator 
     * for a specific target schema.
     *
     * @param resourcePath The path to the OpenAPI resource (e.g., "schemas/openapi.yaml").
     * @param targetSchemaName The name of the schema to extract (e.g., "Category").
     * @param jsonInput The raw JSON input string (stored in LoadedSchema).
     * @return A LoadedSchema record containing the validator, titles map, and input.
     * @throws RuntimeException If reading, parsing, or schema resolution fails.
     */
    public static LoadedSchema getValidatorSchema(String resourcePath, String targetSchemaName, String jsonInput) {
        try {
            Yaml.mapper();
            Json.mapper();

            // 1. Configure and Parse OpenAPI
            ParseOptions options = new ParseOptions();
            options.setResolve(true);
            options.setResolveFully(true);

            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            SwaggerParseResult result = parser.readLocation(resourcePath, null, options); 
            OpenAPI openAPI = result.getOpenAPI();

            //SwaggerParseResult result = new OpenAPIV3Parser().readContents(resourcePath);
            //OpenAPI openAPI = new OpenAPIV3Parser().read(resourcePath, null, options);
            
            if (openAPI == null) {
                // If the resolved object is null, log parsing messages for debugging.
                if (result.getMessages() != null && !result.getMessages().isEmpty()) {
                     LOGGER.error("PARSING_ERROR: Swagger Parser reported messages during loading: {}", result.getMessages());
                }
                throw new RuntimeException("OpenAPI file failed to parse or could not be found.");
            }
            
            if (openAPI.getComponents() == null || openAPI.getComponents().getSchemas() == null) {
                throw new RuntimeException("OpenAPI document loaded, but schema components are missing.");
            }

            // 2. Get Target Schema
            Schema<?> targetSchema = openAPI.getComponents().getSchemas().get(targetSchemaName);

            if (targetSchema == null) {
                throw new RuntimeException("Target schema '" + targetSchemaName + "' not found in OpenAPI components.");
            }
            

            // 3. Create Dynamic Title Map
            Map<String, String> componentTitles = createComponentTitleMap(targetSchema);

            // 4. Serialize, Clean, and Parse to JsonNode for fge Validator
            String schemaJson = JSON_MAPPER.writeValueAsString(targetSchema);
            JsonNode schemaJsonNode = parseJson(schemaJson);

            // Clean $schema and create fge Validator (using getJsonSchema as requested)
            if (schemaJsonNode.isObject()) {
                ((ObjectNode) schemaJsonNode).remove("$schema");
            }
            // Implementation strictly following the user's specific request
            JsonSchema validator = SCHEMA_FACTORY.getJsonSchema(schemaJsonNode);        
            
            return new LoadedSchema(validator, componentTitles, targetSchemaName, jsonInput);

        } catch (IOException e) {
            // EXCEPTION MANAGEMENT: Clear failure point (File I/O or bad file structure)
            LOGGER.error("FILE_IO_ERROR: Failed to load OpenAPI resource: {}", resourcePath, e);
            throw new RuntimeException("File I/O error during schema loading.", e);
        } catch (Exception e) {
            // EXCEPTION MANAGEMENT: Catches all processing errors (e.g., JsonNode parsing)
            LOGGER.error("PROCESSING_FATAL: Critical error in schema resolution for target {}.", targetSchemaName, e);
            throw new RuntimeException("Schema processing failed.", e);
        }
    }

    /**
     * Creates a map associating the allOf pointer (/allOf/N) with the original component's title.
     * (Uses Streams for optimization.)
     */
    private static Map<String, String> createComponentTitleMap(Schema<?> targetSchema) {
    
        if (targetSchema.getAllOf() == null || targetSchema.getAllOf().isEmpty()) {
            return new HashMap<>();
        }
        
        List<Schema> allOfSchemas = targetSchema.getAllOf();

        return IntStream.range(0, allOfSchemas.size())
                .mapToObj(index -> {
                    Schema<?> refSchema = allOfSchemas.get(index);
                    String refString = refSchema.get$ref();
                    String title = refSchema.getTitle(); 

                    String finalTitle = title != null ? title : 
                                        (refString != null && refString.startsWith(SchemaConstants.SCHEMA_COMPONENTS_PREFIX)) 
                                        ? refString.substring(SchemaConstants.SCHEMA_COMPONENTS_PREFIX.length()) 
                                        : "Inline Schema Component";
                    
                    return Map.entry("/allOf/" + index, finalTitle);
                })
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }


    /**
     * Parses a JSON string into a Jackson JsonNode.
     */
    public static JsonNode parseJson(String json) {
        try {
            return JSON_MAPPER.readTree(json);
        } catch (Exception e) {
            LOGGER.error("PARSE_ERROR: Error parsing JSON string: {}", e.getMessage());
            throw new RuntimeException("Error parsing JSON string.", e);
        }
    }


    /**
     * Performs JSON validation, logging the outcome and detailed errors.
     * (Uses the optimized stream pattern for logging.)
     * * @param loadedSchema The record containing the validator, titles, and input JSON.
     * @return {@code true} if validation is successful, {@code false} otherwise.
     */
	public static boolean isJsonValid(LoadedSchema loadedSchema) {

        LOGGER.info("INFO: Starting validation for target: {}", loadedSchema.targetSchemaName());
       
        try {
            // 1. Parse JSON Input
            JsonNode jsonInputNode = parseJson(loadedSchema.jsonInput());

            JsonSchema jsonSchemaValidator = loadedSchema.validator();

            // 2. Perform Validation
            ProcessingReport jsonReport = jsonSchemaValidator.validate(jsonInputNode);

            // 3. Check Result and Log Errors
            if (!jsonReport.isSuccess()) {
                
                LOGGER.warn("VALIDATION_FAILED: Failed for {} payload. Reporting detailed errors...", loadedSchema.targetSchemaName());
                
                // Log all specific, translated errors using the optimized stream method
                SchemaResolver.printRelevantValidationErrors(jsonReport, loadedSchema.titles());
                
                return false;
            }

            // SUCCESS
            LOGGER.info("SUCCESS: Validation successful for {} payload.", loadedSchema.targetSchemaName());
            return true;

        } catch (Exception e) {
            // Catches critical errors during validation (e.g., malformed schema definition itself)
            LOGGER.error("FATAL_ERROR: Schema processing failed during validation for {}.", loadedSchema.targetSchemaName(), e);
            return false;
        }
    }


    /**
     * Logs the relevant validation errors (ERROR/FATAL) by descending into the 'allOf' structure.
     * (Uses the optimized stream pattern for fast logging.)
     */
    public static void printRelevantValidationErrors(ProcessingReport report, Map<String, String> componentTitles) {
        
        Spliterator<ProcessingMessage> spliterator = report.spliterator();
        
        StreamSupport.stream(spliterator, false)
            .filter(error -> error.getLogLevel().ordinal() >= LogLevel.ERROR.ordinal())
            
            .forEach(error -> {
                
                JsonNode errorJson = error.asJson();
                String keyword = errorJson.has("keyword") ? errorJson.get("keyword").asText() : "N/A";
                
                // Logic to descend into the nested 'allOf' error structure
                if ("allOf".equals(keyword) && errorJson.has("reports")) {
                    
                    JsonNode reportsNode = errorJson.get("reports");
                    
                    Spliterator<Map.Entry<String, JsonNode>> mapSpliterator = 
                        Spliterators.spliteratorUnknownSize(reportsNode.fields(), Spliterator.ORDERED);
                    
                    StreamSupport.stream(mapSpliterator, false) 
                        .forEach(entry -> {
                            JsonNode reportNode = entry.getValue(); 
                            
                            if (reportNode.isArray()) {
                                
                                reportNode.spliterator().forEachRemaining(subError -> { 
                                    
                                    if (subError.has("keyword") && "required".equals(subError.get("keyword").asText())) {
                                        
                                        String failedPointer = subError.get("schema").get("pointer").asText();
                                        String missingProps = subError.get("missing").toString();
                                        String level = subError.get("level").toString();
                                        String message = subError.get("message").asText();
                                        
                                        String translatedName = componentTitles.getOrDefault(
                                            failedPointer, 
                                            "Unknown Component (" + failedPointer + ")"
                                        );
                                        
                                        LOGGER.error(
                                            "VALIDATION FAILED: Required property missing. Level: {} | Component: {} | Missing: {} | Details: {}", 
                                            level, translatedName, 
                                            missingProps.replaceAll("\"", ""), 
                                            message
                                        );
                                    }
                                });
                            }
                        });
                } else {
                    // Log other high-level errors
                    LOGGER.error(
                        "GENERIC VALIDATION ERROR: Level={} | Message={} | Keyword={}", 
                        error.getLogLevel().toString(), 
                        error.getMessage(), 
                        keyword
                    );
                }
            });
    }
}