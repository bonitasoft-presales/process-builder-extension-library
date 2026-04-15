package com.bonitasoft.processbuilder.execution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Builds a multipart/related HTTP body (RFC 2387).
 *
 * <p>Assembles two parts:</p>
 * <ol>
 *   <li>JSON metadata (e.g. file name, MIME type, parent folder)</li>
 *   <li>Binary file content</li>
 * </ol>
 *
 * <p>This format is used by Google Drive, Gmail, Cloud Storage, Microsoft Graph,
 * and any API accepting {@code multipart/related} uploads.</p>
 */
public final class MultipartRelatedBuilder {

    private MultipartRelatedBuilder() {}

    /**
     * Result of a multipart build: raw bytes and the full Content-Type header value.
     */
    public record MultipartBody(byte[] content, String contentType) {}

    /**
     * Builds a multipart/related body from JSON metadata and binary file content.
     *
     * @param metadataJson    JSON metadata (Part 1)
     * @param fileContent     Raw binary file content (Part 2)
     * @param fileContentType MIME type of the file (e.g. "application/pdf")
     * @return A {@link MultipartBody} containing the assembled bytes and Content-Type with boundary
     */
    public static MultipartBody build(String metadataJson, byte[] fileContent, String fileContentType) {
        String boundary = "boundary_" + UUID.randomUUID().toString().replace("-", "");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Part 1: JSON metadata
            out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write("Content-Type: application/json; charset=UTF-8\r\n".getBytes(StandardCharsets.UTF_8));
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            out.write(metadataJson.getBytes(StandardCharsets.UTF_8));
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // Part 2: Binary file
            out.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(("Content-Type: " + fileContentType + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            out.write(fileContent);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));

            // Closing boundary
            out.write(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));

            String contentType = "multipart/related; boundary=" + boundary;
            return new MultipartBody(out.toByteArray(), contentType);

        } catch (IOException e) {
            // ByteArrayOutputStream never throws IOException in practice
            throw new IllegalStateException("Failed to build multipart body", e);
        }
    }
}
