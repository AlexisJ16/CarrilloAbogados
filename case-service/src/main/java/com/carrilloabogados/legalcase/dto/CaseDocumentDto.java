package com.carrilloabogados.legalcase.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.carrilloabogados.legalcase.domain.DocumentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CaseDocumentDto implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("documentId")
	private Integer documentId;
	
	@JsonProperty("caseId")
	@NotNull(message = "Case ID must not be null")
	private Integer caseId;
	
	@JsonProperty("documentName")
	@NotBlank(message = "Document name must not be blank")
	@Size(max = 255, message = "Document name must not exceed 255 characters")
	private String documentName;
	
	@JsonProperty("originalFilename")
	@Size(max = 255, message = "Original filename must not exceed 255 characters")
	private String originalFilename;
	
	@JsonProperty("documentType")
	@NotNull(message = "Document type must not be null")
	private DocumentType documentType;
	
	@JsonProperty("filePath")
	@NotBlank(message = "File path must not be blank")
	@Size(max = 500, message = "File path must not exceed 500 characters")
	private String filePath;
	
	@JsonProperty("fileSize")
	private Long fileSize;
	
	@JsonProperty("mimeType")
	@Size(max = 100, message = "MIME type must not exceed 100 characters")
	private String mimeType;
	
	@JsonProperty("description")
	@Size(max = 1000, message = "Description must not exceed 1000 characters")
	private String description;
	
	@JsonProperty("uploadedBy")
	@Size(max = 100, message = "Uploaded by must not exceed 100 characters")
	private String uploadedBy;
	
	@JsonProperty("uploadDate")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message = "Upload date must not be null")
	private LocalDateTime uploadDate;
	
	@JsonProperty("version")
	@Builder.Default
	private Integer version = 1;
	
	@JsonProperty("isConfidential")
	@Builder.Default
	private Boolean isConfidential = false;
	
	@JsonProperty("isSigned")
	@Builder.Default
	private Boolean isSigned = false;
	
	@JsonProperty("isNotarized")
	@Builder.Default
	private Boolean isNotarized = false;
	
	@JsonProperty("expirationDate")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime expirationDate;
	
	@JsonProperty("tags")
	@Size(max = 500, message = "Tags must not exceed 500 characters")
	private String tags;
	
	@JsonProperty("checksum")
	@Size(max = 64, message = "Checksum must not exceed 64 characters")
	private String checksum;
	
	@JsonProperty("isActive")
	@Builder.Default
	private Boolean isActive = true;
	
	@JsonProperty("notes")
	private String notes;
	
	@JsonProperty("createdAt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private java.time.Instant createdAt;
	
	@JsonProperty("updatedAt")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private java.time.Instant updatedAt;
	
}