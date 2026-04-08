package com.bonitasoft.processbuilder.execution;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the JWT Bearer assertion building logic in HttpExecutor.
 */
class HttpExecutorJwtBearerTest {

    /**
     * Generates a test RSA key pair and returns the private key in PEM format.
     */
    private static KeyPair generateRsaKeyPair() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    private static String toPem(java.security.PrivateKey privateKey) {
        String base64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        StringBuilder pem = new StringBuilder("-----BEGIN PRIVATE KEY-----\n");
        for (int i = 0; i < base64.length(); i += 64) {
            pem.append(base64, i, Math.min(i + 64, base64.length())).append("\n");
        }
        pem.append("-----END PRIVATE KEY-----");
        return pem.toString();
    }

    @Nested
    @DisplayName("JWT Construction")
    class JwtConstruction {

        @Test
        void should_produce_three_part_jwt() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt(
                    "sa@project.iam.gserviceaccount.com",
                    "https://www.googleapis.com/auth/drive",
                    "https://oauth2.googleapis.com/token",
                    pem);

            String[] parts = jwt.split("\\.");
            assertThat(parts).hasSize(3);
        }

        @Test
        void should_handle_literal_backslash_n_in_pem_key() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            // Simulate Google SA JSON format: real newlines replaced with literal \n
            String rawPem = toPem(kp.getPrivate());
            String googleStylePem = rawPem.replace("\n", "\\n");

            String jwt = HttpExecutor.buildSignedJwt(
                    "sa@project.iam.gserviceaccount.com", "scope",
                    "https://oauth2.googleapis.com/token", googleStylePem);

            String[] parts = jwt.split("\\.");
            assertThat(parts).hasSize(3);

            // Verify signature is still valid
            byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(kp.getPublic());
            verifier.update((parts[0] + "." + parts[1]).getBytes(StandardCharsets.UTF_8));
            assertThat(verifier.verify(signatureBytes)).isTrue();
        }

        @Test
        void should_have_correct_header() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt(
                    "sa@test.iam.gserviceaccount.com", "scope",
                    "https://oauth2.googleapis.com/token", pem);

            String headerJson = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[0]), StandardCharsets.UTF_8);
            assertThat(headerJson).isEqualTo("{\"alg\":\"RS256\",\"typ\":\"JWT\"}");
        }

        @Test
        void should_have_correct_payload_fields() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt(
                    "sa@project.iam.gserviceaccount.com",
                    "https://www.googleapis.com/auth/drive",
                    "https://oauth2.googleapis.com/token",
                    pem);

            String payloadJson = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]), StandardCharsets.UTF_8);

            assertThat(payloadJson).contains("\"iss\":\"sa@project.iam.gserviceaccount.com\"");
            assertThat(payloadJson).contains("\"scope\":\"https://www.googleapis.com/auth/drive\"");
            assertThat(payloadJson).contains("\"aud\":\"https://oauth2.googleapis.com/token\"");
            assertThat(payloadJson).contains("\"iat\":");
            assertThat(payloadJson).contains("\"exp\":");
        }

        @Test
        void should_set_exp_one_hour_after_iat() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt("sa@test.iam.gserviceaccount.com",
                    "scope", "https://token.url", pem);

            String payloadJson = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]), StandardCharsets.UTF_8);

            // Extract iat and exp values
            long iat = extractNumericClaim(payloadJson, "iat");
            long exp = extractNumericClaim(payloadJson, "exp");

            assertThat(exp - iat).isEqualTo(3600);
        }

        @Test
        void should_handle_null_scope_gracefully() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt("sa@test.iam.gserviceaccount.com",
                    null, "https://token.url", pem);

            String payloadJson = new String(Base64.getUrlDecoder().decode(jwt.split("\\.")[1]), StandardCharsets.UTF_8);
            assertThat(payloadJson).contains("\"scope\":\"\"");
        }
    }

    @Nested
    @DisplayName("JWT Signature Verification")
    class JwtSignature {

        @Test
        void should_produce_valid_rs256_signature() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt("sa@test.iam.gserviceaccount.com",
                    "scope", "https://token.url", pem);

            String[] parts = jwt.split("\\.");
            String signedContent = parts[0] + "." + parts[1];
            byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

            // Verify with the public key
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify((RSAPublicKey) kp.getPublic());
            verifier.update(signedContent.getBytes(StandardCharsets.UTF_8));

            assertThat(verifier.verify(signatureBytes)).isTrue();
        }

        @Test
        void should_fail_verification_with_wrong_key() throws Exception {
            KeyPair kp1 = generateRsaKeyPair();
            KeyPair kp2 = generateRsaKeyPair();
            String pem = toPem(kp1.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt("sa@test.iam.gserviceaccount.com",
                    "scope", "https://token.url", pem);

            String[] parts = jwt.split("\\.");
            String signedContent = parts[0] + "." + parts[1];
            byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify((RSAPublicKey) kp2.getPublic());
            verifier.update(signedContent.getBytes(StandardCharsets.UTF_8));

            assertThat(verifier.verify(signatureBytes)).isFalse();
        }

        @Test
        void should_use_base64url_without_padding() throws Exception {
            KeyPair kp = generateRsaKeyPair();
            String pem = toPem(kp.getPrivate());

            String jwt = HttpExecutor.buildSignedJwt("sa@test.iam.gserviceaccount.com",
                    "scope", "https://token.url", pem);

            for (String part : jwt.split("\\.")) {
                assertThat(part).doesNotContain("=");
                assertThat(part).doesNotContain("+");
                assertThat(part).doesNotContain("/");
            }
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        void should_throw_on_invalid_private_key() {
            String invalidPem = "-----BEGIN PRIVATE KEY-----\nNOT_VALID_BASE64_KEY\n-----END PRIVATE KEY-----";

            assertThatThrownBy(() -> HttpExecutor.buildSignedJwt(
                    "sa@test.iam.gserviceaccount.com", "scope", "https://token.url", invalidPem))
                    .isInstanceOf(Exception.class);
        }

        @Test
        void should_throw_on_empty_private_key() {
            assertThatThrownBy(() -> HttpExecutor.buildSignedJwt(
                    "sa@test.iam.gserviceaccount.com", "scope", "https://token.url", ""))
                    .isInstanceOf(Exception.class);
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private static long extractNumericClaim(String json, String claim) {
        String prefix = "\"" + claim + "\":";
        int start = json.indexOf(prefix) + prefix.length();
        int end = json.indexOf(",", start);
        if (end < 0) end = json.indexOf("}", start);
        return Long.parseLong(json.substring(start, end).trim());
    }
}
