package com.carrilloabogados.client.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.carrilloabogados.client.dto.AddressDto;
import com.carrilloabogados.client.exception.wrapper.AddressNotFoundException;
import com.carrilloabogados.client.helper.AddressMappingHelper;
import com.carrilloabogados.client.repository.AddressRepository;
import com.carrilloabogados.client.service.AddressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	
	private final AddressRepository addressRepository;
	
	@Override
	public List<AddressDto> findAll() {
		log.info("*** AddressDto List, service; fetch all addresss *");
		return this.addressRepository.findAll()
				.stream()
					.map(AddressMappingHelper::map)
					.distinct()
					.collect(Collectors.toUnmodifiableList());
	}
	
	@Override
	public AddressDto findById(final Integer addressId) {
		log.info("*** AddressDto, service; fetch address by id *");
		return this.addressRepository.findById(addressId)
				.map(AddressMappingHelper::map)
				.orElseThrow(() -> new AddressNotFoundException("#### Address with id: %d not found! ####".formatted(addressId)));
	}
	
	@Override
	public AddressDto save(final AddressDto addressDto) {
		log.info("*** AddressDto, service; save address *");
		return AddressMappingHelper.map(this.addressRepository.save(AddressMappingHelper.map(addressDto)));
	}
	
	@Override
	public AddressDto update(final AddressDto addressDto) {
		log.info("*** AddressDto, service; update address *");
		return AddressMappingHelper.map(this.addressRepository.save(AddressMappingHelper.map(addressDto)));
	}
	
	@Override
	public AddressDto update(final Integer addressId, final AddressDto addressDto) {
		log.info("*** AddressDto, service; update address with addressId *");
		return AddressMappingHelper.map(this.addressRepository.save(
				AddressMappingHelper.map(this.findById(addressId))));
	}
	
	@Override
	public void deleteById(final Integer addressId) {
		log.info("*** Void, service; delete address by id *");
		this.addressRepository.deleteById(addressId);
	}
	
	
	
}










