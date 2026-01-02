package com.carrilloabogados.client.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
 * Bean Validation Tests - Verify @NotNull, @Email, @Size, @Pattern annotations
 * work correctly.
 * 
 * These tests ensure that invalid data is rejected before reaching the business
 * logic.
 * 
 * @author Alexis - Carrillo Abogados
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("✅ Bean Validation Tests")
class BeanValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LEADS_ENDPOINT = "/api/leads";

    @Nested
    @DisplayName("📧 Email Validation")
    class EmailValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should reject null or empty email")
        void shouldRejectNullOrEmptyEmail(String email) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            if (email != null) {
                json.put("email", email);
            }
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "notanemail",
                // "missing@domain" is technically valid per RFC 5321 for local domains
                "@nodomain.com",
                "spaces in@email.com",
                "double@@at.com",
                ".startswithdot@email.com",
                "email@",
                "email@.com"
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", invalidEmail);
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "valid@email.com",
                "user.name@domain.co",
                "user+tag@company.org",
                "test@subdomain.domain.com",
                "valid123@test.io"
        })
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats(String validEmail) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", validEmail);
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("👤 Nombre Validation")
    class NombreValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should reject null or empty nombre")
        void shouldRejectNullOrEmptyNombre(String nombre) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            if (nombre != null) {
                json.put("nombre", nombre);
            }
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should reject nombre with only whitespace")
        void shouldRejectWhitespaceOnlyNombre() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "   ");
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("📞 Telefono Validation")
    class TelefonoValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "+57 300 123 4567",
                "+573001234567",
                "3001234567",
                "+1 555 123 4567",
                "(555) 123-4567"
        })
        @DisplayName("Should accept valid phone formats")
        void shouldAcceptValidPhoneFormats(String phone) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test@test.com");
            json.put("telefono", phone);
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("⚖️ Servicio Validation")
    class ServicioValidationTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "TRADEMARK_LAW",
                "ADMINISTRATIVE_LAW",
                "COMPETITION_LAW",
                "CORPORATE_LAW",
                "TELECOMMUNICATIONS_LAW"
        })
        @DisplayName("Should accept valid practice areas")
        void shouldAcceptValidServicioValues(String servicio) throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test" + System.currentTimeMillis() + "@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", servicio);
            json.put("mensaje", "Test message");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should handle unknown servicio gracefully")
        void shouldHandleUnknownServicio() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "UNKNOWN_SERVICE");
            json.put("mensaje", "Test message");

            // Should either accept (stored as string) or reject (400)
            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(result -> {
                        int status = result.getResponse().getStatus();
                        assert status == 201 || status == 400
                                : "Unknown servicio should be accepted or rejected, not cause server error";
                    });
        }
    }

    @Nested
    @DisplayName("📝 Mensaje Validation")
    class MensajeValidationTests {

        @Test
        @DisplayName("Should accept mensaje with minimum length")
        void shouldAcceptMinimumLengthMensaje() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test" + System.currentTimeMillis() + "@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "Hi"); // Very short message

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("Should accept mensaje with maximum reasonable length")
        void shouldAcceptReasonableLengthMensaje() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "test" + System.currentTimeMillis() + "@test.com");
            json.put("telefono", "+57 300 123 4567");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje", "A".repeat(2000)); // Long but reasonable message

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("🔄 Complete Valid Lead")
    class CompleteValidLeadTests {

        @Test
        @DisplayName("Should accept a complete valid lead with all fields")
        void shouldAcceptCompleteValidLead() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Juan Carlos Pérez García");
            json.put("email", "juan.perez" + System.currentTimeMillis() + "@empresa.com.co");
            json.put("telefono", "+57 315 987 6543");
            json.put("empresa", "Innovatech Solutions SAS");
            json.put("cargo", "Director de Operaciones");
            json.put("servicio", "TRADEMARK_LAW");
            json.put("mensaje",
                    "Necesito asesoría urgente para registrar la marca de mi nueva línea de productos tecnológicos. La empresa tiene presencia en 3 países.");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombre").value("Juan Carlos Pérez García"))
                    .andExpect(jsonPath("$.leadId").exists())
                    .andExpect(jsonPath("$.leadStatus").value("NUEVO"))
                    .andExpect(jsonPath("$.leadCategory").value("COLD"));
        }

        @Test
        @DisplayName("Should accept lead with only required fields")
        void shouldAcceptLeadWithOnlyRequiredFields() throws Exception {
            ObjectNode json = objectMapper.createObjectNode();
            json.put("nombre", "Test User");
            json.put("email", "minimal" + System.currentTimeMillis() + "@test.com");
            json.put("servicio", "CORPORATE_LAW");
            json.put("mensaje", "Minimal lead test");

            mockMvc.perform(post(LEADS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json.toString()))
                    .andExpect(status().isCreated());
        }
    }
}
