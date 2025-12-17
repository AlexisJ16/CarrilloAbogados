package com.carrilloabogados.legalcase.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrilloabogados.legalcase.domain.CaseDocument;
import com.carrilloabogados.legalcase.domain.DocumentType;

public interface CaseDocumentRepository extends JpaRepository<CaseDocument, Integer> {

	/**
	 * Find all documents for a specific legal case
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndIsActiveTrueOrderByUploadDateDesc(Integer caseId);
	
	/**
	 * Find all documents for a specific legal case with pagination
	 */
	Page<CaseDocument> findByLegalCaseCaseIdAndIsActiveTrue(Integer caseId, Pageable pageable);
	
	/**
	 * Find document by file path
	 */
	Optional<CaseDocument> findByFilePath(String filePath);
	
	/**
	 * Find documents by document type
	 */
	List<CaseDocument> findByDocumentTypeAndIsActiveTrueOrderByUploadDateDesc(DocumentType documentType);
	
	/**
	 * Find documents by document type for a specific case
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndDocumentTypeAndIsActiveTrueOrderByUploadDateDesc(
			Integer caseId, DocumentType documentType);
	
	/**
	 * Find documents uploaded by specific user
	 */
	List<CaseDocument> findByUploadedByAndIsActiveTrueOrderByUploadDateDesc(String uploadedBy);
	
	/**
	 * Find confidential documents for a case
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndIsConfidentialTrueAndIsActiveTrueOrderByUploadDateDesc(Integer caseId);
	
	/**
	 * Find signed documents for a case
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndIsSignedTrueAndIsActiveTrueOrderByUploadDateDesc(Integer caseId);
	
	/**
	 * Find notarized documents for a case
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndIsNotarizedTrueAndIsActiveTrueOrderByUploadDateDesc(Integer caseId);
	
	/**
	 * Find documents with expiration dates
	 */
	List<CaseDocument> findByExpirationDateIsNotNullAndIsActiveTrueOrderByExpirationDateAsc();
	
	/**
	 * Find expired documents
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE cd.expirationDate IS NOT NULL " +
	       "AND cd.expirationDate < CURRENT_TIMESTAMP AND cd.isActive = true " +
	       "ORDER BY cd.expirationDate DESC")
	List<CaseDocument> findExpiredDocuments();
	
	/**
	 * Find documents expiring soon (within specified days)
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE cd.expirationDate IS NOT NULL " +
	       "AND cd.expirationDate BETWEEN CURRENT_TIMESTAMP AND :expirationThreshold " +
	       "AND cd.isActive = true ORDER BY cd.expirationDate ASC")
	List<CaseDocument> findDocumentsExpiringSoon(@Param("expirationThreshold") LocalDateTime expirationThreshold);
	
	/**
	 * Find documents by upload date range
	 */
	List<CaseDocument> findByUploadDateBetweenAndIsActiveTrueOrderByUploadDateDesc(
			LocalDateTime startDate, LocalDateTime endDate);
	
	/**
	 * Find documents by tags
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE cd.tags LIKE %:tag% AND cd.isActive = true ORDER BY cd.uploadDate DESC")
	List<CaseDocument> findByTag(@Param("tag") String tag);
	
	/**
	 * Search documents by name or description
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE " +
	       "(cd.documentName LIKE %:searchText% OR cd.description LIKE %:searchText%) " +
	       "AND cd.isActive = true ORDER BY cd.uploadDate DESC")
	List<CaseDocument> searchDocuments(@Param("searchText") String searchText);
	
	/**
	 * Find documents by version
	 */
	List<CaseDocument> findByLegalCaseCaseIdAndVersionAndIsActiveTrueOrderByUploadDateDesc(
			Integer caseId, Integer version);
	
	/**
	 * Find latest version of documents with same name for a case
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE cd.legalCase.caseId = :caseId " +
	       "AND cd.documentName = :documentName AND cd.isActive = true " +
	       "ORDER BY cd.version DESC")
	List<CaseDocument> findLatestVersionByName(@Param("caseId") Integer caseId, 
			@Param("documentName") String documentName);
	
	/**
	 * Count documents for a case
	 */
	long countByLegalCaseCaseIdAndIsActiveTrue(Integer caseId);
	
	/**
	 * Count documents by type for a case
	 */
	long countByLegalCaseCaseIdAndDocumentTypeAndIsActiveTrue(Integer caseId, DocumentType documentType);
	
	/**
	 * Get total file size for a case
	 */
	@Query("SELECT COALESCE(SUM(cd.fileSize), 0) FROM CaseDocument cd WHERE cd.legalCase.caseId = :caseId AND cd.isActive = true")
	Long getTotalFileSizeByCase(@Param("caseId") Integer caseId);
	
	/**
	 * Find recent documents across all cases
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE cd.isActive = true ORDER BY cd.uploadDate DESC")
	List<CaseDocument> findRecentDocuments(Pageable pageable);
	
	/**
	 * Find documents by multiple criteria
	 */
	@Query("SELECT cd FROM CaseDocument cd WHERE " +
	       "(:caseId IS NULL OR cd.legalCase.caseId = :caseId) AND " +
	       "(:documentType IS NULL OR cd.documentType = :documentType) AND " +
	       "(:uploadedBy IS NULL OR cd.uploadedBy = :uploadedBy) AND " +
	       "(:isConfidential IS NULL OR cd.isConfidential = :isConfidential) AND " +
	       "(:searchText IS NULL OR cd.documentName LIKE %:searchText% OR cd.description LIKE %:searchText%) " +
	       "AND cd.isActive = true ORDER BY cd.uploadDate DESC")
	Page<CaseDocument> searchDocuments(
			@Param("caseId") Integer caseId,
			@Param("documentType") DocumentType documentType,
			@Param("uploadedBy") String uploadedBy,
			@Param("isConfidential") Boolean isConfidential,
			@Param("searchText") String searchText,
			Pageable pageable
	);
	
	/**
	 * Find duplicate documents by checksum
	 */
	List<CaseDocument> findByChecksumAndIsActiveTrue(String checksum);
	
}