package com.selimhorri.app.config.template;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TemplateConfig {

	@LoadBalanced
	@Bean
	RestTemplate restTemplateBean() {
		return new RestTemplate();
	}
	
	
	
}










