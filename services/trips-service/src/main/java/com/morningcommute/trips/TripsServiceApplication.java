package com.morningcommute.trips;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.morningcommute.trips")
// main application entry point
public class TripsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripsServiceApplication.class, args);
	}

}
