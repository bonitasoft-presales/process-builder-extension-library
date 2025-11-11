package com.bonitasoft.processbuilder.records;

import com.github.fge.jsonschema.main.JsonSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for the LoadedSchema record, focusing on data integrity, immutability,
 * and correct handling of its components (validator, titles, names, and input JSON).
 */
@ExtendWith(MockitoExtension.class)
class LoadedSchemaTest {

    // Mock the dependency (JsonSchema) as we only need an instance, not its behavior
    @Mock
    private JsonSchema mockValidator;

    private static final String TARGET_NAME = "MyProcessSchema";
    private static final String RAW_JSON = "{\"type\": \"object\"}";
    
    private Map<String, String> mutableTitlesMap;
    private LoadedSchema loadedSchema;

    @BeforeEach
    void setUp() {
        // Initialize a mutable map to simulate external data input
        mutableTitlesMap = new HashMap<>();
        mutableTitlesMap.put("/properties/name", "NameField");
        mutableTitlesMap.put("/properties/age", "AgeField");

        // Instantiate the record using the mutable map
        loadedSchema = new LoadedSchema(
                mockValidator,
                mutableTitlesMap,
                TARGET_NAME,
                RAW_JSON
        );
    }

    // ------------------- Basic Value Verification Tests -------------------

    @Test
    void shouldInitializeCorrectlyWithGivenValues() {
        // Assert scalar values are correctly set
        assertThat(loadedSchema.validator()).isEqualTo(mockValidator);
        assertThat(loadedSchema.targetSchemaName()).isEqualTo(TARGET_NAME);
        assertThat(loadedSchema.jsonInput()).isEqualTo(RAW_JSON);
        
        // Assert the map content is correctly captured
        assertThat(loadedSchema.titles()).hasSize(2)
                                       .containsEntry("/properties/name", "NameField");
    }

    // ------------------- Immutability (Defensive Copy) Tests -------------------

    /**
     * Tests the defensive copying logic in the compact constructor.
     */
    @Test
    void constructorShouldCreateDefensiveCopyOfTitlesMap() {
        // GIVEN the record is constructed, WHEN the external mutableTitlesMap is modified
        mutableTitlesMap.put("new_key", "NewValue");
        
        // THEN the internal state of the record must remain unchanged
        // (i.e., the record's map should still have only 2 entries)
        assertThat(loadedSchema.titles()).hasSize(2)
                .doesNotContainKey("new_key");
    }

    /**
     * Tests that the titles() accessor returns an unmodifiable map, preventing mutation 
     * from external code.
     */
    @Test
    void titlesAccessorShouldReturnUnmodifiableView() {
        // Retrieve the map view
        Map<String, String> returnedTitles = loadedSchema.titles();
        
        // Assert the returned map is unmodifiable (throws UOE on mutation attempt)
        assertThatThrownBy(() -> returnedTitles.put("malicious_key", "fail"))
            .isInstanceOf(UnsupportedOperationException.class);
            
        // Assert that the internal state remains safe after the failed mutation attempt
        assertThat(loadedSchema.titles()).hasSize(2);
    }
    
    // ------------------- Edge Case Tests -------------------

    @Test
    void shouldHandleNullTitlesMapGracefully() {
        // Note: Map.copyOf(null) throws NullPointerException, so we test the scenario 
        // where an empty map is passed, as null maps are usually prevented by design.
        
        // If the record were designed to accept null, the validation should be here.
        // Since Map.copyOf is used, passing a null map will result in an NPE from the constructor.
        
        assertThatThrownBy(() -> new LoadedSchema(mockValidator, null, TARGET_NAME, RAW_JSON))
                .isInstanceOf(NullPointerException.class); 
    }
    
    @Test
    void shouldHandleEmptyTitlesMap() {
        // GIVEN an empty map is passed to the constructor
        LoadedSchema emptySchema = new LoadedSchema(
                mockValidator, 
                Collections.emptyMap(), 
                TARGET_NAME, 
                RAW_JSON
        );
        
        // THEN the internal map should be empty and unmodifiable
        assertThat(emptySchema.titles()).isNotNull().isEmpty();
        
        // Assert accessor still returns an unmodifiable list
        assertThatThrownBy(() -> emptySchema.titles().put("test", "value"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}