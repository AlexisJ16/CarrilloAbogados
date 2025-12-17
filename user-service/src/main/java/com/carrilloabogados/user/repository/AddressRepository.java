package com.carrilloabogados.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.user.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	
	
	
}
