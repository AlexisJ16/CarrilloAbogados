package com.carrilloabogados.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.user.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByCredentialUsername(final String username);
	
}
