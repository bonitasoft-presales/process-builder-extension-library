package com.bonitasoft.processbuilder.execution;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartRelatedBuilderTest {

    @Test
    void should_build_multipart_related_body() {
        String metadata = "{\"name\":\"test.pdf\",\"mimeType\":\"application/pdf\"}";
        byte[] fileContent = "fake-pdf-content".getBytes(StandardCharsets.UTF_8);

        MultipartRelatedBuilder.MultipartBody result =
                MultipartRelatedBuilder.build(metadata, fileContent, "application/pdf");

        assertThat(result.contentType()).startsWith("multipart/related; boundary=boundary_");

        String body = new String(result.content(), StandardCharsets.UTF_8);
        String boundary = result.contentType().replace("multipart/related; boundary=", "");

        // Part 1: JSON metadata
        assertThat(body).contains("--" + boundary);
        assertThat(body).contains("Content-Type: application/json; charset=UTF-8");
        assertThat(body).contains(metadata);

        // Part 2: file content
        assertThat(body).contains("Content-Type: application/pdf");
        assertThat(body).contains("fake-pdf-content");

        // Closing boundary
        assertThat(body).endsWith("--" + boundary + "--");
    }

    @Test
    void should_handle_binary_file_content() {
        String metadata = "{\"name\":\"image.png\"}";
        byte[] binaryContent = new byte[]{0x00, 0x01, (byte) 0xFF, (byte) 0xFE, 0x42};

        MultipartRelatedBuilder.MultipartBody result =
                MultipartRelatedBuilder.build(metadata, binaryContent, "image/png");

        assertThat(result.content()).isNotEmpty();
        assertThat(result.contentType()).contains("multipart/related");

        // Verify binary content is embedded
        byte[] content = result.content();
        boolean found = false;
        for (int i = 0; i <= content.length - binaryContent.length; i++) {
            boolean match = true;
            for (int j = 0; j < binaryContent.length; j++) {
                if (content[i + j] != binaryContent[j]) {
                    match = false;
                    break;
                }
            }
            if (match) { found = true; break; }
        }
        assertThat(found).as("Binary content should be present in multipart body").isTrue();
    }

    @Test
    void should_generate_unique_boundaries() {
        String metadata = "{}";
        byte[] file = new byte[]{0x01};

        MultipartRelatedBuilder.MultipartBody result1 =
                MultipartRelatedBuilder.build(metadata, file, "text/plain");
        MultipartRelatedBuilder.MultipartBody result2 =
                MultipartRelatedBuilder.build(metadata, file, "text/plain");

        assertThat(result1.contentType()).isNotEqualTo(result2.contentType());
    }
}
