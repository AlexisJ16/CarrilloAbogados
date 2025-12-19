package com.carrilloabogados.client.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.domain.Lead;
import com.carrilloabogados.client.dto.LeadDto;
import com.carrilloabogados.client.exception.wrapper.LeadNotFoundException;
import com.carrilloabogados.client.repository.LeadRepository;
import com.carrilloabogados.client.service.impl.LeadServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadService Unit Tests")
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private LeadServiceImpl leadService;

    private Lead testLead;
    private LeadDto testLeadDto;
    private UUID testLeadId;

    @BeforeEach
    void setUp() {
        testLeadId = UUID.randomUUID();

        testLead = Lead.builder()
                .leadId(testLeadId)
                .nombre("Juan Perez")
                .email("juan.perez@empresa.com")
                .telefono("+57 3001234567")
                .empresa("Tech Solutions SAS")
                .cargo("Gerente General")
                .servicio("TRADEMARK_LAW")
                .mensaje("Necesito registrar una marca")
                .leadScore(0)
                .leadCategory(LeadCategory.COLD)
                .leadStatus(LeadStatus.NUEVO)
                .source(LeadSource.WEBSITE)
                .emailsSent(0)
                .emailsOpened(0)
                .emailsClicked(0)
                .build();
        testLead.setCreatedAt(Instant.now());

        testLeadDto = LeadDto.builder()
                .leadId(testLeadId)
                .nombre("Juan Perez")
                .email("juan.perez@empresa.com")
                .telefono("+57 3001234567")
                .empresa("Tech Solutions SAS")
                .cargo("Gerente General")
                .servicio("TRADEMARK_LAW")
                .mensaje("Necesito registrar una marca")
                .source(LeadSource.WEBSITE)
                .build();
    }

    @Test
    @DisplayName("findAll - Should return all leads")
    void findAll_shouldReturnAllLeads() {
        List<Lead> leads = List.of(testLead);
        given(leadRepository.findAll()).willReturn(leads);

        List<LeadDto> result = leadService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Juan Perez");
        verify(leadRepository).findAll();
    }

    @Test
    @DisplayName("findAll - Should return empty list when no leads exist")
    void findAll_shouldReturnEmptyListWhenNoLeads() {
        given(leadRepository.findAll()).willReturn(Collections.emptyList());

        List<LeadDto> result = leadService.findAll();

        assertThat(result).isEmpty();
        verify(leadRepository).findAll();
    }

    @Test
    @DisplayName("findById - Should find lead by id")
    void findById_shouldFindLeadById() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));

        LeadDto result = leadService.findById(testLeadId);

        assertThat(result).isNotNull();
        assertThat(result.getLeadId()).isEqualTo(testLeadId);
        assertThat(result.getNombre()).isEqualTo("Juan Perez");
        verify(leadRepository).findById(testLeadId);
    }

    @Test
    @DisplayName("findById - Should throw exception when lead not found")
    void findById_shouldThrowExceptionWhenNotFound() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.findById(testLeadId))
                .isInstanceOf(LeadNotFoundException.class)
                .hasMessageContaining(testLeadId.toString());
    }

    @Test
    @DisplayName("capture - Should capture new lead and publish event")
    void capture_shouldCaptureNewLeadAndPublishEvent() {
        given(leadRepository.existsByEmail(testLeadDto.getEmail())).willReturn(false);
        given(leadRepository.save(any(Lead.class))).willReturn(testLead);
        given(eventPublisher.publishLeadCaptured(any())).willReturn(true);

        LeadDto result = leadService.capture(testLeadDto);

        assertThat(result).isNotNull();

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository).save(leadCaptor.capture());
        Lead capturedLead = leadCaptor.getValue();
        assertThat(capturedLead.getLeadStatus()).isEqualTo(LeadStatus.NUEVO);
        assertThat(capturedLead.getLeadCategory()).isEqualTo(LeadCategory.COLD);
        assertThat(capturedLead.getLeadScore()).isEqualTo(0);

        verify(eventPublisher).publishLeadCaptured(any());
    }

    @Test
    @DisplayName("capture - Should return existing lead when email already exists")
    void capture_shouldReturnExistingLeadWhenEmailExists() {
        given(leadRepository.existsByEmail(testLeadDto.getEmail())).willReturn(true);
        given(leadRepository.findByEmail(testLeadDto.getEmail())).willReturn(Optional.of(testLead));

        LeadDto result = leadService.capture(testLeadDto);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testLeadDto.getEmail());
        verify(leadRepository, never()).save(any(Lead.class));
        verify(eventPublisher, never()).publishLeadCaptured(any());
    }

    @Test
    @DisplayName("capture - Should set default values when capturing lead")
    void capture_shouldSetDefaultValuesWhenCapturing() {
        LeadDto minimalDto = LeadDto.builder()
                .nombre("Maria Garcia")
                .email("maria.garcia@test.com")
                .build();

        given(leadRepository.existsByEmail(minimalDto.getEmail())).willReturn(false);
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        leadService.capture(minimalDto);

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository).save(leadCaptor.capture());
        Lead capturedLead = leadCaptor.getValue();
        assertThat(capturedLead.getLeadStatus()).isEqualTo(LeadStatus.NUEVO);
        assertThat(capturedLead.getLeadCategory()).isEqualTo(LeadCategory.COLD);
        assertThat(capturedLead.getSource()).isEqualTo(LeadSource.WEBSITE);
    }

    @Test
    @DisplayName("updateScoring - Should update lead to HOT category")
    void updateScoring_shouldUpdateLeadToHotCategory() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.updateScoring(testLeadId, 75, LeadCategory.HOT);

        assertThat(result.getLeadScore()).isEqualTo(75);

        ArgumentCaptor<Lead> leadCaptor = ArgumentCaptor.forClass(Lead.class);
        verify(leadRepository).save(leadCaptor.capture());
        assertThat(leadCaptor.getValue().getLeadScore()).isEqualTo(75);
    }

    @Test
    @DisplayName("updateScoring - Should update lead to WARM category")
    void updateScoring_shouldUpdateLeadToWarmCategory() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.updateScoring(testLeadId, 55, LeadCategory.WARM);

        assertThat(result.getLeadScore()).isEqualTo(55);
        assertThat(result.getLeadCategory()).isEqualTo(LeadCategory.WARM);
    }

    @Test
    @DisplayName("updateScoring - Should throw exception for non-existent lead")
    void updateScoring_shouldThrowExceptionForNonExistentLead() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.updateScoring(testLeadId, 70, LeadCategory.HOT))
                .isInstanceOf(LeadNotFoundException.class);
    }

    @Test
    @DisplayName("updateStatus - Should update lead status to NURTURING")
    void updateStatus_shouldUpdateLeadStatusToNurturing() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.updateStatus(testLeadId, LeadStatus.NURTURING);

        assertThat(result.getLeadStatus()).isEqualTo(LeadStatus.NURTURING);
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    @DisplayName("updateStatus - Should update lead status to MQL")
    void updateStatus_shouldUpdateLeadStatusToMQL() {
        testLead.setLeadStatus(LeadStatus.NURTURING);
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.updateStatus(testLeadId, LeadStatus.MQL);

        assertThat(result.getLeadStatus()).isEqualTo(LeadStatus.MQL);
    }

    @Test
    @DisplayName("updateEmailEngagement - Should update email engagement metrics")
    void updateEmailEngagement_shouldUpdateMetrics() {
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.updateEmailEngagement(testLeadId, 5, 3, 1);

        assertThat(result.getEmailsSent()).isEqualTo(5);
        assertThat(result.getEmailsOpened()).isEqualTo(3);
        assertThat(result.getEmailsClicked()).isEqualTo(1);
        assertThat(result.getLastEngagement()).isNotNull();
    }

    @Test
    @DisplayName("convertToClient - Should convert lead to client")
    void convertToClient_shouldConvertLeadToClient() {
        Integer clientId = 123;
        given(leadRepository.findById(testLeadId)).willReturn(Optional.of(testLead));
        given(leadRepository.save(any(Lead.class))).willAnswer(invocation -> invocation.getArgument(0));

        LeadDto result = leadService.convertToClient(testLeadId, clientId);

        assertThat(result.getLeadStatus()).isEqualTo(LeadStatus.CONVERTIDO);
        assertThat(result.getClientId()).isEqualTo(clientId);
        assertThat(result.getConvertedAt()).isNotNull();
    }

    @Test
    @DisplayName("findHotLeadsPending - Should find hot leads pending notification")
    void findHotLeadsPending_shouldFindHotLeads() {
        testLead.setLeadCategory(LeadCategory.HOT);
        List<Lead> hotLeads = List.of(testLead);
        given(leadRepository.findHotLeadsPendingNotification()).willReturn(hotLeads);

        List<LeadDto> result = leadService.findHotLeadsPending();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLeadCategory()).isEqualTo(LeadCategory.HOT);
        verify(leadRepository).findHotLeadsPendingNotification();
    }

    @Test
    @DisplayName("findInactiveLeads - Should find inactive leads")
    void findInactiveLeads_shouldFindInactiveLeads() {
        testLead.setLastEngagement(Instant.now().minus(35, ChronoUnit.DAYS));
        List<Lead> inactiveLeads = List.of(testLead);
        given(leadRepository.findInactiveLeads(any(Instant.class))).willReturn(inactiveLeads);

        List<LeadDto> result = leadService.findInactiveLeads(30);

        assertThat(result).hasSize(1);
        verify(leadRepository).findInactiveLeads(any(Instant.class));
    }

    @Test
    @DisplayName("search - Should search leads by name or company")
    void search_shouldSearchLeads() {
        List<Lead> leads = List.of(testLead);
        given(leadRepository.searchByNombreOrEmpresa("Tech")).willReturn(leads);

        List<LeadDto> result = leadService.search("Tech");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmpresa()).contains("Tech");
        verify(leadRepository).searchByNombreOrEmpresa("Tech");
    }

    @Test
    @DisplayName("Lead - Should identify corporate email")
    void lead_shouldIdentifyCorporateEmail() {
        Lead corporateLead = Lead.builder()
                .email("ceo@empresa.com")
                .build();

        assertThat(corporateLead.hasCorpEmail()).isTrue();
    }

    @Test
    @DisplayName("Lead - Should identify non-corporate email")
    void lead_shouldIdentifyNonCorporateEmail() {
        Lead personalLead = Lead.builder()
                .email("persona@gmail.com")
                .build();

        assertThat(personalLead.hasCorpEmail()).isFalse();
    }

    @Test
    @DisplayName("Lead - Should identify C-Level roles")
    void lead_shouldIdentifyCLevelRoles() {
        Lead ceoLead = Lead.builder().cargo("CEO").build();
        Lead gerenteLead = Lead.builder().cargo("Gerente General").build();
        Lead directorLead = Lead.builder().cargo("Director Comercial").build();

        assertThat(ceoLead.isCLevel()).isTrue();
        assertThat(gerenteLead.isCLevel()).isTrue();
        assertThat(directorLead.isCLevel()).isTrue();
    }

    @Test
    @DisplayName("Lead - Should identify high-value services")
    void lead_shouldIdentifyHighValueServices() {
        Lead marcaLead = Lead.builder().servicio("marca").build();
        Lead litigioLead = Lead.builder().servicio("LITIGIO").build();

        assertThat(marcaLead.isHighValueService()).isTrue();
        assertThat(litigioLead.isHighValueService()).isTrue();
    }

    @Test
    @DisplayName("deleteById - Should delete lead by id")
    void deleteById_shouldDeleteLeadById() {
        given(leadRepository.existsById(testLeadId)).willReturn(true);
        doNothing().when(leadRepository).deleteById(testLeadId);

        leadService.deleteById(testLeadId);

        verify(leadRepository).existsById(testLeadId);
        verify(leadRepository).deleteById(testLeadId);
    }

    @Test
    @DisplayName("deleteById - Should throw exception for non-existent lead")
    void deleteById_shouldThrowExceptionForNonExistentLead() {
        given(leadRepository.existsById(testLeadId)).willReturn(false);

        assertThatThrownBy(() -> leadService.deleteById(testLeadId))
                .isInstanceOf(LeadNotFoundException.class);
        verify(leadRepository, never()).deleteById(any());
    }
}
