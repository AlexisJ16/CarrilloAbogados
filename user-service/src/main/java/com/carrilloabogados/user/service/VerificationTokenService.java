package com.carrilloabogados.user.service;

import java.util.List;

import com.carrilloabogados.user.dto.VerificationTokenDto;

public interface VerificationTokenService {
	
	List<VerificationTokenDto> findAll();
	VerificationTokenDto findById(final Integer verificationTokenId);
	VerificationTokenDto save(final VerificationTokenDto verificationTokenDto);
	VerificationTokenDto update(final VerificationTokenDto verificationTokenDto);
	VerificationTokenDto update(final Integer verificationTokenId, final VerificationTokenDto verificationTokenDto);
	void deleteById(final Integer verificationTokenId);
	
}










