package com.carrilloabogados.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.user.domain.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
	
	
	
}
