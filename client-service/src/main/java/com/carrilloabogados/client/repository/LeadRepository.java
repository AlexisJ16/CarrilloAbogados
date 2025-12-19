package com.carrilloabogados.client.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrilloabogados.client.constant.LeadCategory;
import com.carrilloabogados.client.constant.LeadSource;
import com.carrilloabogados.client.constant.LeadStatus;
import com.carrilloabogados.client.domain.Lead;

/**
 * Repository para la entidad Lead.
 * 
 * Incluye métodos de búsqueda optimizados para:
 * - Filtrado por categoría (HOT/WARM/COLD)
 * - Filtrado por estado del funnel
 * - Búsqueda de leads inactivos para reactivación
 */
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    // ========== Búsquedas básicas ==========

    Optional<Lead> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Lead> findByLeadCategory(LeadCategory category);

    List<Lead> findByLeadStatus(LeadStatus status);

    List<Lead> findBySource(LeadSource source);

    // ========== Búsquedas con paginación ==========

    Page<Lead> findByLeadCategory(LeadCategory category, Pageable pageable);

    Page<Lead> findByLeadStatus(LeadStatus status, Pageable pageable);

    Page<Lead> findByLeadCategoryAndLeadStatus(LeadCategory category, LeadStatus status, Pageable pageable);

    // ========== Búsquedas para n8n integration ==========

    /**
     * Encuentra leads HOT pendientes de notificación.
     * Usados para alertar al abogado inmediatamente.
     */
    @Query("SELECT l FROM Lead l WHERE l.leadCategory = 'HOT' AND l.leadStatus = 'NUEVO' ORDER BY l.createdAt DESC")
    List<Lead> findHotLeadsPendingNotification();

    /**
     * Encuentra leads que no han tenido engagement en los últimos N días.
     * Para reactivación por MW#2.
     */
    @Query("SELECT l FROM Lead l WHERE l.lastEngagement < :cutoffDate AND l.leadStatus NOT IN ('CONVERTIDO', 'CHURNED')")
    List<Lead> findInactiveLeads(@Param("cutoffDate") Instant cutoffDate);

    /**
     * Encuentra leads en nurturing para la secuencia de emails.
     */
    @Query("SELECT l FROM Lead l WHERE l.leadStatus = 'NURTURING' AND l.emailsSent < 12 ORDER BY l.lastContact ASC NULLS FIRST")
    List<Lead> findLeadsForNurturing();

    /**
     * Cuenta leads por categoría para métricas.
     */
    @Query("SELECT l.leadCategory, COUNT(l) FROM Lead l GROUP BY l.leadCategory")
    List<Object[]> countByCategory();

    /**
     * Cuenta leads por fuente para análisis de canales.
     */
    @Query("SELECT l.source, COUNT(l) FROM Lead l GROUP BY l.source")
    List<Object[]> countBySource();

    /**
     * Encuentra leads por rango de score.
     */
    @Query("SELECT l FROM Lead l WHERE l.leadScore >= :minScore AND l.leadScore <= :maxScore")
    List<Lead> findByScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    /**
     * Encuentra leads creados en un período.
     */
    @Query("SELECT l FROM Lead l WHERE l.createdAt >= :startDate AND l.createdAt <= :endDate ORDER BY l.createdAt DESC")
    List<Lead> findByCreatedAtBetween(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);

    /**
     * Busca leads por nombre o empresa (para autocompletado).
     */
    @Query("SELECT l FROM Lead l WHERE LOWER(l.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.empresa) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Lead> searchByNombreOrEmpresa(@Param("query") String query);

}
