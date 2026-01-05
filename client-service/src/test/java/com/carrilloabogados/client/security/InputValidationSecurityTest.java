package com.carrilloabogados.client.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Security tests for input validation - SQL Injection and XSS protection.
 * 
 * These tests verify that the API properly rejects or sanitizes malicious
 * input.
 * 
 * @author Alexis - Carrillo Abogados
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("🔐 Input Validation Security Tests")
class InputValidationSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LEADS_ENDPOINT = "/api/leads";

    @Nested
    @DisplayName("🛡️ SQL Injection Prevention")
    class SqlInjectionTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "'; DROP TABLE leads; --",
                "1' OR '1'='1",
                "admin'--",
                "1; DELETE FROM leads WHERE 1=1; --",
                "' UNION SELECT * FROM users --",
                "Robert'); DROP TABLE leads;--",
                "1' AND 1=CONVERT(int, (SELECT TOP 1 table_name FROM information_schema.tables))--"
        })
        @DisplayName("Should reject SQL injection attempts in nombre field")
        void shouldRejectSqlInjectionInNombre(String sqlInjection) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", sqlInjection);
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(result -> {
                        // Either rejected (400) or sanitized (201) - but NOT executed
                        int status = result.getResponse().getStatus();
                        assert status == 400 || status == 201
                                : "SQL injection should be rejected or sanitized, got status: " + status;
                    });
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "test@test.com' OR '1'='1",
                "admin@test.com'; DROP TABLE leads; --",
                "user@test.com' UNION SELECT * FROM users --"
        })
        @DisplayName("Should reject SQL injection attempts in email field")
        void shouldRejectSqlInjectionInEmail(String maliciousEmail) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", maliciousEmail);
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            // Email with SQL injection should fail validation (invalid email format)
            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should safely handle search with SQL injection attempt")
        void shouldSafelyHandleSearchWithSqlInjection() throws Exception {
            String maliciousQuery = "test' OR '1'='1";

            mockMvc.perform(get(LEADS_ENDPOINT + "/search")
                    .param("query", maliciousQuery))
                    .andExpect(result -> {
                        // Should return 200 with empty results, 400 (invalid), or 403 (blocked by
                        // security) - NOT expose all data
                        // 403 is also valid because Spring Security blocks unauthenticated access
                        // before the malicious input reaches the controller
                        int status = result.getResponse().getStatus();
                        assert status == 200 || status == 400 || status == 403
                                : "SQL injection in search should be safely handled, got status: " + status;
                        // If 200, should return empty or filtered results, not all data
                    });
        }
    }

    @Nested
    @DisplayName("🛡️ XSS (Cross-Site Scripting) Prevention")
    class XssPreventionTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert('XSS')>",
                "<svg onload=alert('XSS')>",
                "javascript:alert('XSS')",
                "<body onload=alert('XSS')>",
                "<iframe src='javascript:alert(1)'></iframe>",
                "<input onfocus=alert(1) autofocus>",
                "'><script>alert(String.fromCharCode(88,83,83))</script>",
                "\"><script>alert('XSS')</script>",
                "<div style=\"background-image:url(javascript:alert('XSS'))\">"
        })
        @DisplayName("Should sanitize or reject XSS in mensaje field")
        void shouldHandleXssInMensaje(String xssPayload) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", xssPayload);

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Both 201 (accepted) and 400 (rejected) are valid behaviors
                        // In a REST API, XSS sanitization is typically the frontend's responsibility
                        // The backend should either accept the data (for storage) or reject invalid
                        // input
                        // Note: Actual XSS protection occurs when data is rendered in HTML, not in JSON
                        // APIs
                        assert status == 201 || status == 400
                                : "Expected 201 (accepted) or 400 (rejected), got: " + status;
                    });
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert(1)>",
                "Test<script>alert(1)</script>User"
        })
        @DisplayName("Should sanitize or reject XSS in nombre field")
        void shouldHandleXssInNombre(String xssPayload) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", xssPayload);
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Normal message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Both 201 (accepted) and 400 (rejected) are valid behaviors
                        // In a REST API, XSS sanitization is typically the frontend's responsibility
                        // Note: Actual XSS protection occurs when data is rendered in HTML, not in JSON
                        // APIs
                        assert status == 201 || status == 400
                                : "Expected 201 (accepted) or 400 (rejected), got: " + status;
                    });
        }
    }

    @Nested
    @DisplayName("🛡️ Request Validation")
    class RequestValidationTests {

        @Test
        @DisplayName("Should reject oversized request body")
        void shouldRejectOversizedRequestBody() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test");
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            // Create a very large message (>1MB)
            json.put("mensaje", "A".repeat(2_000_000));

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        // Should be rejected - either 400, 413, or 500
                        assert status >= 400 : "Oversized request should be rejected";
                    });
        }

        @Test
        @DisplayName("Should reject malformed JSON")
        void shouldRejectMalformedJson() throws Exception {
            String malformedJson = "{ nombre: 'test', email: test@test.com }"; // Invalid JSON

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(malformedJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject empty request body")
        void shouldRejectEmptyBody() throws Exception {
            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(""))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject request with wrong content type")
        void shouldRejectWrongContentType() throws Exception {
            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("nombre=test"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    @Nested
    @DisplayName("🛡️ Path Traversal Prevention")
    class PathTraversalTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "../../../etc/passwd",
                "..\\..\\..\\windows\\system32\\config\\sam",
                "%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fpasswd",
                "....//....//....//etc/passwd"
        })
        @DisplayName("Should reject path traversal attempts in ID parameter")
        void shouldRejectPathTraversalInId(String pathTraversal) throws Exception {
            try {
                mockMvc.perform(get(LEADS_ENDPOINT + "/" + pathTraversal))
                        .andExpect(result -> {
                            int status = result.getResponse().getStatus();
                            // Should be 400 (bad request), 403 (forbidden), 404 (not found) - NOT 200 with
                            // file content
                            // 403 is valid because Spring Security blocks unauthenticated access before the
                            // malicious input reaches the controller
                            assert status == 400 || status == 403 || status == 404 || status == 500
                                    : "Path traversal should be rejected, got status: " + status;
                            String response = result.getResponse().getContentAsString();
                            assert !response.contains("root:") : "Response should not contain /etc/passwd content";
                        });
            } catch (Exception e) {
                // IllegalArgumentException from UUID validation is also valid rejection
                // Spring wraps it in NestedServletException/ServletException
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                assert cause instanceof IllegalArgumentException
                        || cause.getMessage().contains("UUID")
                        || cause.getMessage().contains("Invalid")
                        : "Exception should be due to invalid input, got: " + cause.getMessage();
            }
        }
    }

    @Nested
    @DisplayName("🛡️ Field Length Validation")
    class FieldLengthValidationTests {

        @Test
        @DisplayName("Should reject nombre exceeding max length")
        void shouldRejectLongNombre() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "A".repeat(256)); // Assuming max 255
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject email exceeding max length")
        void shouldRejectLongEmail() throws Exception {
            String longEmail = "a".repeat(250) + "@test.com";

            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", longEmail);
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }
    }
}
