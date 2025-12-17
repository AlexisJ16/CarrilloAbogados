package com.carrilloabogados.client.config.mapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Configuration
public class MapperConfig {

	@Bean
	ObjectMapper objectMapperBean() {
		return new JsonMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	
	
}










