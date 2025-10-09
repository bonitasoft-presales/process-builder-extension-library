package com.bonitasoft.processbuilder.records;

import java.util.Map;

import com.github.fge.jsonschema.main.JsonSchema;

public record LoadedSchema (
    JsonSchema validator,
    Map<String, String> titles,
    String targetSchemaName,
    String jsonInput
) {}