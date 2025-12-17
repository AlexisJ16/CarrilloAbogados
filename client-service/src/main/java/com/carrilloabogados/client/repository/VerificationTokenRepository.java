package com.carrilloabogados.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.client.domain.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
	
	
	
}
