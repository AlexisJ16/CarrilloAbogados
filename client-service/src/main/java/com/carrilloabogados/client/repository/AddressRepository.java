package com.carrilloabogados.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrilloabogados.client.domain.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	
	
	
}
