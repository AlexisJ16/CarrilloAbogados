package com.carrilloabogados.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CalendarServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalendarServiceApplication.class, args);
	}

}