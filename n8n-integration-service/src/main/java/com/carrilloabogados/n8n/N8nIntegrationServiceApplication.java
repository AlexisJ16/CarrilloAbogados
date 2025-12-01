package com.carrilloabogados.n8n;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class N8nIntegrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(N8nIntegrationServiceApplication.class, args);
	}

}