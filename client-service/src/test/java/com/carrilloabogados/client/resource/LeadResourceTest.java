package com.carrilloabogados.client.resource;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.dto.LeadDto;
import com.carrilloabogados.client.exception.wrapper.LeadNotFoundException;
import com.carrilloabogados.client.service.LeadService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Integration tests for LeadResource REST endpoints.
 * Tests HTTP request/response handling and JSON serialization.
 * 
 * Note: The controller returns DtoCollectionResponse wrapper for list
 * endpoints,
 * so JSON paths use "$.collection" prefix.
 *
 * @author Alexis - Carrillo Abogados
 */
@WebMvcTest(LeadResource.class)
@DisplayName("LeadResource REST Controller Tests")
class LeadResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeadService leadService;

    private LeadDto testLeadDto;
    private UUID testLeadId;

    @BeforeEach
    void setUp() {
        testLeadId = UUID.randomUUID();

        testLeadDto = LeadDto.builder()
                .leadId(testLeadId)
                .nombre("Juan Perez")
                .email("juan.perez@empresa.com")
                .telefono("+57 3001234567")
                .empresa("Tech Solutions SAS")
                .cargo("Gerente General")
                .servicio("TRADEMARK_LAW")
                .mensaje("Necesito registrar una marca para mi empresa")
                .leadScore(0)
                .leadCategory(LeadCategory.COLD)
                .leadStatus(LeadStatus.NUEVO)
                .source(LeadSource.WEBSITE)
                .emailsSent(0)
                .emailsOpened(0)
                .emailsClicked(0)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/leads - Capture Lead")
    class CaptureLeadTests {

        @Test
        @DisplayName("Should capture lead successfully")
        void shouldCaptureLeadSuccessfully() throws Exception {
            LeadDto inputDto = LeadDto.builder()
                    .nombre("Maria Garcia")
                    .email("maria@empresa.com")
                    .telefono("+57 3009876543")
                    .empresa("Consulting Corp")
                    .servicio("COMPETITION_LAW")
                    .mensaje("Consulta sobre competencia desleal")
                    .source(LeadSource.WEBSITE)
                    .build();

            LeadDto savedDto = LeadDto.builder()
                    .leadId(UUID.randomUUID())
                    .nombre("Maria Garcia")
                    .email("maria@empresa.com")
                    .telefono("+57 3009876543")
                    .empresa("Consulting Corp")
                    .servicio("COMPETITION_LAW")
                    .mensaje("Consulta sobre competencia desleal")
                    .leadScore(0)
                    .leadCategory(LeadCategory.COLD)
                    .leadStatus(LeadStatus.NUEVO)
                    .source(LeadSource.WEBSITE)
                    .createdAt(Instant.now())
                    .build();

            given(leadService.capture(any(LeadDto.class))).willReturn(savedDto);

            mockMvc.perform(post("/api/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.nombre", is("Maria Garcia")))
                    .andExpect(jsonPath("$.email", is("maria@empresa.com")))
                    .andExpect(jsonPath("$.leadStatus", is("NUEVO")))
                    .andExpect(jsonPath("$.leadCategory", is("COLD")));

            verify(leadService).capture(any(LeadDto.class));
        }

        @Test
        @DisplayName("Should return 400 for invalid input - missing required fields")
        void shouldReturn400ForInvalidInput() throws Exception {
            String invalidJson = "{}";

            mockMvc.perform(post("/api/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/leads - List Leads")
    class ListLeadsTests {

        @Test
        @DisplayName("Should return all leads wrapped in collection")
        void shouldReturnAllLeads() throws Exception {
            List<LeadDto> leads = List.of(testLeadDto);
            given(leadService.findAll()).willReturn(leads);

            mockMvc.perform(get("/api/leads"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collection", hasSize(1)))
                    .andExpect(jsonPath("$.collection[0].nombre", is("Juan Perez")))
                    .andExpect(jsonPath("$.collection[0].email", is("juan.perez@empresa.com")));

            verify(leadService).findAll();
        }

        @Test
        @DisplayName("Should return empty collection when no leads")
        void shouldReturnEmptyList() throws Exception {
            given(leadService.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/api/leads"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collection", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/leads/{id} - Get Lead by ID")
    class GetLeadByIdTests {

        @Test
        @DisplayName("Should return lead by id")
        void shouldReturnLeadById() throws Exception {
            given(leadService.findById(testLeadId)).willReturn(testLeadDto);

            mockMvc.perform(get("/api/leads/{id}", testLeadId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.leadId", is(testLeadId.toString())))
                    .andExpect(jsonPath("$.nombre", is("Juan Perez")));

            verify(leadService).findById(testLeadId);
        }

        @Test
        @DisplayName("Should return 400 when lead not found (handled by ApiExceptionHandler)")
        void shouldReturn400WhenLeadNotFound() throws Exception {
            given(leadService.findById(testLeadId))
                    .willThrow(new LeadNotFoundException("Lead not found: " + testLeadId));

            // Note: ApiExceptionHandler returns BAD_REQUEST for RuntimeExceptions
            mockMvc.perform(get("/api/leads/{id}", testLeadId))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/leads/{id} - Update Lead")
    class UpdateLeadTests {

        @Test
        @DisplayName("Should update lead successfully")
        void shouldUpdateLeadSuccessfully() throws Exception {
            LeadDto updateDto = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .telefono("+57 3112223344")
                    .empresa("Updated Corp")
                    .build();

            LeadDto updatedDto = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .telefono("+57 3112223344")
                    .empresa("Updated Corp")
                    .leadScore(0)
                    .leadCategory(LeadCategory.COLD)
                    .leadStatus(LeadStatus.NUEVO)
                    .build();

            given(leadService.update(eq(testLeadId), any(LeadDto.class))).willReturn(updatedDto);

            mockMvc.perform(put("/api/leads/{id}", testLeadId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.empresa", is("Updated Corp")));

            verify(leadService).update(eq(testLeadId), any(LeadDto.class));
        }
    }

    @Nested
    @DisplayName("DELETE /api/leads/{id} - Delete Lead")
    class DeleteLeadTests {

        @Test
        @DisplayName("Should delete lead successfully")
        void shouldDeleteLeadSuccessfully() throws Exception {
            doNothing().when(leadService).deleteById(testLeadId);

            mockMvc.perform(delete("/api/leads/{id}", testLeadId))
                    .andExpect(status().isNoContent());

            verify(leadService).deleteById(testLeadId);
        }

        @Test
        @DisplayName("Should return 400 when deleting non-existent lead")
        void shouldReturn400WhenDeletingNonExistent() throws Exception {
            doThrow(new LeadNotFoundException("Lead not found: " + testLeadId))
                    .when(leadService).deleteById(testLeadId);

            mockMvc.perform(delete("/api/leads/{id}", testLeadId))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/leads/{id}/scoring - Update Scoring")
    class UpdateScoringTests {

        @Test
        @DisplayName("Should update lead scoring")
        void shouldUpdateLeadScoring() throws Exception {
            LeadDto scoredDto = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .leadScore(75)
                    .leadCategory(LeadCategory.HOT)
                    .leadStatus(LeadStatus.NUEVO)
                    .build();

            given(leadService.updateScoring(testLeadId, 75, LeadCategory.HOT)).willReturn(scoredDto);

            mockMvc.perform(patch("/api/leads/{id}/scoring", testLeadId)
                    .param("score", "75")
                    .param("category", "HOT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.leadScore", is(75)))
                    .andExpect(jsonPath("$.leadCategory", is("HOT")));

            verify(leadService).updateScoring(testLeadId, 75, LeadCategory.HOT);
        }
    }

    @Nested
    @DisplayName("PATCH /api/leads/{id}/status - Update Status")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update lead status")
        void shouldUpdateLeadStatus() throws Exception {
            LeadDto statusDto = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .leadStatus(LeadStatus.NURTURING)
                    .build();

            given(leadService.updateStatus(testLeadId, LeadStatus.NURTURING)).willReturn(statusDto);

            mockMvc.perform(patch("/api/leads/{id}/status", testLeadId)
                    .param("status", "NURTURING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.leadStatus", is("NURTURING")));

            verify(leadService).updateStatus(testLeadId, LeadStatus.NURTURING);
        }
    }

    @Nested
    @DisplayName("PATCH /api/leads/{id}/engagement - Update Engagement")
    class UpdateEngagementTests {

        @Test
        @DisplayName("Should update email engagement metrics")
        void shouldUpdateEmailEngagement() throws Exception {
            LeadDto engagedDto = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .emailsSent(10)
                    .emailsOpened(5)
                    .emailsClicked(2)
                    .lastEngagement(Instant.now())
                    .build();

            given(leadService.updateEmailEngagement(testLeadId, 10, 5, 2)).willReturn(engagedDto);

            mockMvc.perform(patch("/api/leads/{id}/engagement", testLeadId)
                    .param("sent", "10")
                    .param("opened", "5")
                    .param("clicked", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.emailsSent", is(10)))
                    .andExpect(jsonPath("$.emailsOpened", is(5)))
                    .andExpect(jsonPath("$.emailsClicked", is(2)));

            verify(leadService).updateEmailEngagement(testLeadId, 10, 5, 2);
        }
    }

    @Nested
    @DisplayName("POST /api/leads/{id}/convert - Convert to Client")
    class ConvertToClientTests {

        @Test
        @DisplayName("Should convert lead to client")
        void shouldConvertLeadToClient() throws Exception {
            Integer clientId = 123;
            LeadDto convertedLead = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .leadStatus(LeadStatus.CONVERTIDO)
                    .clientId(clientId)
                    .convertedAt(Instant.now())
                    .build();

            given(leadService.convertToClient(testLeadId, clientId)).willReturn(convertedLead);

            mockMvc.perform(post("/api/leads/{id}/convert", testLeadId)
                    .param("clientId", clientId.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.leadStatus", is("CONVERTIDO")))
                    .andExpect(jsonPath("$.clientId", is(clientId)));

            verify(leadService).convertToClient(testLeadId, clientId);
        }
    }

    @Nested
    @DisplayName("GET /api/leads/category/{category} - Get Leads by Category")
    class GetLeadsByCategoryTests {

        @Test
        @DisplayName("Should return leads by category wrapped in collection")
        void shouldReturnLeadsByCategory() throws Exception {
            LeadDto hotLead = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .leadCategory(LeadCategory.HOT)
                    .leadScore(80)
                    .build();
            List<LeadDto> hotLeads = List.of(hotLead);

            given(leadService.findByCategory(LeadCategory.HOT)).willReturn(hotLeads);

            mockMvc.perform(get("/api/leads/category/{category}", "HOT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collection", hasSize(1)))
                    .andExpect(jsonPath("$.collection[0].leadCategory", is("HOT")));

            verify(leadService).findByCategory(LeadCategory.HOT);
        }
    }

    @Nested
    @DisplayName("GET /api/leads/hot - Get Hot Leads Pending Notification")
    class GetHotLeadsPendingTests {

        @Test
        @DisplayName("Should return hot leads pending notification wrapped in collection")
        void shouldReturnHotLeadsPending() throws Exception {
            LeadDto hotLead = LeadDto.builder()
                    .leadId(testLeadId)
                    .nombre("Juan Perez")
                    .email("juan.perez@empresa.com")
                    .leadCategory(LeadCategory.HOT)
                    .leadScore(90)
                    .build();

            given(leadService.findHotLeadsPending()).willReturn(List.of(hotLead));

            mockMvc.perform(get("/api/leads/hot"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collection", hasSize(1)))
                    .andExpect(jsonPath("$.collection[0].leadCategory", is("HOT")));

            verify(leadService).findHotLeadsPending();
        }
    }

    @Nested
    @DisplayName("GET /api/leads/search - Search Leads")
    class SearchLeadsTests {

        @Test
        @DisplayName("Should search leads by query")
        void shouldSearchLeads() throws Exception {
            given(leadService.search("Tech")).willReturn(List.of(testLeadDto));

            // Note: Parameter name is 'query' not 'q'
            mockMvc.perform(get("/api/leads/search")
                    .param("query", "Tech"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collection", hasSize(1)))
                    .andExpect(jsonPath("$.collection[0].empresa", containsString("Tech")));

            verify(leadService).search("Tech");
        }
    }
}
