package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BaronesaTinyApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaronesaTinyApiApplication.class, args);
	}

}
