package com.selimhorri.app.business.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.selimhorri.app.business.user.model.CredentialDto;
import com.selimhorri.app.business.user.model.response.CredentialUserServiceCollectionDtoResponse;

@FeignClient(name = "USER-SERVICE", contextId = "credentialClientService", path = "/user-service/api/credentials")
public interface CredentialClientService {
	
	@GetMapping
	ResponseEntity<CredentialUserServiceCollectionDtoResponse> findAll();
	
	@GetMapping("/{credentialId}")
	ResponseEntity<CredentialDto> findById(
			@PathVariable 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String credentialId);
	
	@GetMapping("/username/{username}")
	ResponseEntity<CredentialDto> findByUsername(
			@PathVariable 
			@NotBlank(message = "*Input must not blank!**") 
			@Valid final String username);
	
	@PostMapping
	ResponseEntity<CredentialDto> save(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@PutMapping
	ResponseEntity<CredentialDto> update(
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@PutMapping("/{credentialId}")
	ResponseEntity<CredentialDto> update(
			@PathVariable 
			@NotBlank(message = "*Input must not blank!**") final String credentialId, 
			@RequestBody 
			@NotNull(message = "*Input must not NULL!**") 
			@Valid final CredentialDto credentialDto);
	
	@DeleteMapping("/{credentialId}")
	ResponseEntity<Boolean> deleteById(@PathVariable @NotBlank(message = "*Input must not blank!**") @Valid final String credentialId);
	
}










