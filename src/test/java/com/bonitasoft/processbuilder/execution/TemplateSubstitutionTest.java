package com.bonitasoft.processbuilder.execution;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateSubstitutionTest {

    @Test
    void should_substitute_single_param() {
        String result = TemplateSubstitution.substitute(
                "https://api.example.com/{{version}}/users",
                Map.of("version", "v2"));
        assertThat(result).isEqualTo("https://api.example.com/v2/users");
    }

    @Test
    void should_substitute_multiple_params() {
        String result = TemplateSubstitution.substitute(
                "{{baseUrl}}/api/{{version}}/{{resource}}",
                Map.of("baseUrl", "https://api.com", "version", "v3", "resource", "orders"));
        assertThat(result).isEqualTo("https://api.com/api/v3/orders");
    }

    @Test
    void should_keep_unresolved_placeholders() {
        String result = TemplateSubstitution.substitute(
                "Hello {{name}}, your ID is {{id}}",
                Map.of("name", "Alice"));
        assertThat(result).isEqualTo("Hello Alice, your ID is {{id}}");
    }

    @Test
    void should_return_template_when_params_null() {
        String template = "https://api.com/{{version}}";
        assertThat(TemplateSubstitution.substitute(template, null)).isEqualTo(template);
    }

    @Test
    void should_return_template_when_params_empty() {
        String template = "https://api.com/{{version}}";
        assertThat(TemplateSubstitution.substitute(template, Collections.emptyMap())).isEqualTo(template);
    }

    @Test
    void should_return_null_when_template_null() {
        assertThat(TemplateSubstitution.substitute(null, Map.of("a", "b"))).isNull();
    }

    @Test
    void should_return_empty_when_template_empty() {
        assertThat(TemplateSubstitution.substitute("", Map.of("a", "b"))).isEmpty();
    }

    @Test
    void should_handle_special_regex_chars_in_value() {
        String result = TemplateSubstitution.substitute(
                "filter={{query}}",
                Map.of("query", "$100.00 (total)"));
        assertThat(result).isEqualTo("filter=$100.00 (total)");
    }

    @Test
    void should_trim_param_name() {
        String result = TemplateSubstitution.substitute(
                "{{ name }}", Map.of("name", "Alice"));
        assertThat(result).isEqualTo("Alice");
    }

    // ========================================================================
    // buildFinalUrl tests
    // ========================================================================

    @Test
    void should_join_base_and_path() {
        assertThat(TemplateSubstitution.buildFinalUrl("https://api.com", "/users"))
                .isEqualTo("https://api.com/users");
    }

    @Test
    void should_handle_trailing_slash_on_base() {
        assertThat(TemplateSubstitution.buildFinalUrl("https://api.com/", "users"))
                .isEqualTo("https://api.com/users");
    }

    @Test
    void should_handle_both_slashes() {
        assertThat(TemplateSubstitution.buildFinalUrl("https://api.com/", "/users"))
                .isEqualTo("https://api.com/users");
    }

    @Test
    void should_return_base_when_path_empty() {
        assertThat(TemplateSubstitution.buildFinalUrl("https://api.com", ""))
                .isEqualTo("https://api.com");
    }

    @Test
    void should_return_base_when_path_null() {
        assertThat(TemplateSubstitution.buildFinalUrl("https://api.com", null))
                .isEqualTo("https://api.com");
    }
}
