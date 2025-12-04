package com.carrilloabogados.client.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.carrilloabogados.client.dto.VerificationTokenDto;
import com.carrilloabogados.client.exception.wrapper.VerificationTokenNotFoundException;
import com.carrilloabogados.client.helper.VerificationTokenMappingHelper;
import com.carrilloabogados.client.repository.VerificationTokenRepository;
import com.carrilloabogados.client.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
	
	private final VerificationTokenRepository verificationTokenRepository;
	
	@Override
	public List<VerificationTokenDto> findAll() {
		log.info("*** VerificationTokenDto List, service; fetch all verificationTokens *");
		return this.verificationTokenRepository.findAll()
				.stream()
					.map(VerificationTokenMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public VerificationTokenDto findById(final Integer verificationTokenId) {
		log.info("*** VerificationTokenDto, service; fetch verificationToken by ids *");
		return this.verificationTokenRepository.findById(verificationTokenId)
				.map(VerificationTokenMappingHelper::map)
				.orElseThrow(() -> new VerificationTokenNotFoundException("#### VerificationToken with id: %d not found! ####"
				.formatted(verificationTokenId)));
	}
	
	@Override
	public VerificationTokenDto save(final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; save verificationToken *");
		return VerificationTokenMappingHelper.map(this.verificationTokenRepository
				.save(VerificationTokenMappingHelper.map(verificationTokenDto)));
	}
	
	@Override
	public VerificationTokenDto update(final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; update verificationToken *");
		return VerificationTokenMappingHelper.map(this.verificationTokenRepository
				.save(VerificationTokenMappingHelper.map(verificationTokenDto)));
	}
	
	@Override
	public VerificationTokenDto update(final Integer verificationTokenId, final VerificationTokenDto verificationTokenDto) {
		log.info("*** VerificationTokenDto, service; update verificationToken with verificationTokenId *");
		return VerificationTokenMappingHelper.map(this.verificationTokenRepository.save(
				VerificationTokenMappingHelper.map(this.findById(verificationTokenId))));
	}
	
	@Override
	public void deleteById(final Integer verificationTokenId) {
		log.info("*** Void, service; delete verificationToken by id *");
		this.verificationTokenRepository.deleteById(verificationTokenId);
	}
	
	
	
}










