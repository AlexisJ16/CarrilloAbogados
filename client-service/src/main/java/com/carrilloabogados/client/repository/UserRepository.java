package com.carrilloabogados.client.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.client.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByCredentialUsername(final String username);
	
}
