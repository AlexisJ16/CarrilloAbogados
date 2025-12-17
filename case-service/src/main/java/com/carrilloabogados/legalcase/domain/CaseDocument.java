package com.carrilloabogados.legalcase.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "case_documents")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public final class CaseDocument extends AbstractMappedEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "document_id", unique = true, nullable = false, updatable = false)
	private Integer documentId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "case_id", nullable = false)
	@NotNull(message = "Legal case must not be null")
	private LegalCase legalCase;
	
	@Column(name = "document_name", nullable = false)
	@NotBlank(message = "Document name must not be blank")
	@Size(max = 255, message = "Document name must not exceed 255 characters")
	private String documentName;
	
	@Column(name = "original_filename")
	@Size(max = 255, message = "Original filename must not exceed 255 characters")
	private String originalFilename;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "document_type", nullable = false)
	@NotNull(message = "Document type must not be null")
	private DocumentType documentType;
	
	@Column(name = "file_path", nullable = false)
	@NotBlank(message = "File path must not be blank")
	@Size(max = 500, message = "File path must not exceed 500 characters")
	private String filePath;
	
	@Column(name = "file_size")
	private Long fileSize; // in bytes
	
	@Column(name = "mime_type")
	@Size(max = 100, message = "MIME type must not exceed 100 characters")
	private String mimeType;
	
	@Column(name = "description", columnDefinition = "TEXT")
	@Size(max = 1000, message = "Description must not exceed 1000 characters")
	private String description;
	
	@Column(name = "uploaded_by")
	@Size(max = 100, message = "Uploaded by must not exceed 100 characters")
	private String uploadedBy;
	
	@Column(name = "upload_date", nullable = false)
	@NotNull(message = "Upload date must not be null")
	private LocalDateTime uploadDate;
	
	@Column(name = "version")
	@Builder.Default
	private Integer version = 1;
	
	@Column(name = "is_confidential")
	@Builder.Default
	private Boolean isConfidential = false;
	
	@Column(name = "is_signed")
	@Builder.Default
	private Boolean isSigned = false;
	
	@Column(name = "is_notarized")
	@Builder.Default
	private Boolean isNotarized = false;
	
	@Column(name = "expiration_date")
	private LocalDateTime expirationDate;
	
	@Column(name = "tags")
	@Size(max = 500, message = "Tags must not exceed 500 characters")
	private String tags; // comma-separated tags
	
	@Column(name = "checksum")
	@Size(max = 64, message = "Checksum must not exceed 64 characters")
	private String checksum; // for file integrity verification
	
	@Column(name = "is_active")
	@Builder.Default
	private Boolean isActive = true;
	
	@Column(name = "notes", columnDefinition = "TEXT")
	private String notes;
	
}