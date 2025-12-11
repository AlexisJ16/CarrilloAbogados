package com.carrilloabogados.legalcase.helper;

import com.carrilloabogados.legalcase.domain.CaseDocument;
import com.carrilloabogados.legalcase.dto.CaseDocumentDto;

public interface CaseDocumentMappingHelper {

	public static CaseDocumentDto map(final CaseDocument caseDocument) {
		return CaseDocumentDto.builder()
				.documentId(caseDocument.getDocumentId())
				.caseId(caseDocument.getLegalCase() != null ? 
						caseDocument.getLegalCase().getCaseId() : null)
				.documentName(caseDocument.getDocumentName())
				.originalFilename(caseDocument.getOriginalFilename())
				.documentType(caseDocument.getDocumentType())
				.filePath(caseDocument.getFilePath())
				.fileSize(caseDocument.getFileSize())
				.mimeType(caseDocument.getMimeType())
				.description(caseDocument.getDescription())
				.uploadedBy(caseDocument.getUploadedBy())
				.uploadDate(caseDocument.getUploadDate())
				.version(caseDocument.getVersion())
				.isConfidential(caseDocument.getIsConfidential())
				.isSigned(caseDocument.getIsSigned())
				.isNotarized(caseDocument.getIsNotarized())
				.expirationDate(caseDocument.getExpirationDate())
				.tags(caseDocument.getTags())
				.checksum(caseDocument.getChecksum())
				.isActive(caseDocument.getIsActive())
				.notes(caseDocument.getNotes())
				.createdAt(caseDocument.getCreatedAt())
				.updatedAt(caseDocument.getUpdatedAt())
				.build();
	}

	public static CaseDocument map(final CaseDocumentDto caseDocumentDto) {
		return CaseDocument.builder()
				.documentId(caseDocumentDto.getDocumentId())
				.documentName(caseDocumentDto.getDocumentName())
				.originalFilename(caseDocumentDto.getOriginalFilename())
				.documentType(caseDocumentDto.getDocumentType())
				.filePath(caseDocumentDto.getFilePath())
				.fileSize(caseDocumentDto.getFileSize())
				.mimeType(caseDocumentDto.getMimeType())
				.description(caseDocumentDto.getDescription())
				.uploadedBy(caseDocumentDto.getUploadedBy())
				.uploadDate(caseDocumentDto.getUploadDate())
				.version(caseDocumentDto.getVersion())
				.isConfidential(caseDocumentDto.getIsConfidential())
				.isSigned(caseDocumentDto.getIsSigned())
				.isNotarized(caseDocumentDto.getIsNotarized())
				.expirationDate(caseDocumentDto.getExpirationDate())
				.tags(caseDocumentDto.getTags())
				.checksum(caseDocumentDto.getChecksum())
				.isActive(caseDocumentDto.getIsActive())
				.notes(caseDocumentDto.getNotes())
				.build();
	}
	
}