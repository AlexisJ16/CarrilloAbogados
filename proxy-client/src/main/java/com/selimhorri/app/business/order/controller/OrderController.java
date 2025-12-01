package com.selimhorri.app.business.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.selimhorri.app.business.order.model.OrderDto;
import com.selimhorri.app.business.order.model.response.OrderOrderServiceDtoCollectionResponse;
import com.selimhorri.app.business.order.service.OrderClientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
	
	private final OrderClientService orderClientService;
	
	@GetMapping
	public ResponseEntity<OrderOrderServiceDtoCollectionResponse> findAll() {
		return ResponseEntity.ok(this.orderClientService.findAll().getBody());
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<OrderDto> findById(
			@PathVariable 
			@NotBlank(message = "Input must not be blank!") 
			@Valid final String orderId) {
		return ResponseEntity.ok(this.orderClientService.findById(orderId).getBody());
	}
	
	@PostMapping
	public ResponseEntity<OrderDto> save(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final OrderDto orderDto) {
		return ResponseEntity.ok(this.orderClientService.save(orderDto).getBody());
	}
	
	@PutMapping
	public ResponseEntity<OrderDto> update(
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final OrderDto orderDto) {
		return ResponseEntity.ok(this.orderClientService.update(orderDto).getBody());
	}
	
	@PutMapping("/{orderId}")
	public ResponseEntity<OrderDto> update(
			@PathVariable
			@NotBlank(message = "Input must not be blank!")
			@Valid final String orderId,
			@RequestBody 
			@NotNull(message = "Input must not be NULL!") 
			@Valid final OrderDto orderDto) {
		return ResponseEntity.ok(this.orderClientService.update(orderId, orderDto).getBody());
	}
	
	@DeleteMapping("/{orderId}")
	public ResponseEntity<Boolean> deleteById(@PathVariable final String orderId) {
		this.orderClientService.deleteById(orderId).getBody();
		return ResponseEntity.ok(true);
	}
	
	
	
}
