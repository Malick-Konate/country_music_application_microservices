package com.konate.music_application.catalogsubdomain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CatalogServiceApplication {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


    public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}

}
