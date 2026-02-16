package com.nbaData.nba_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NbaApiApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(NbaApiApplication.class);
		app.setWebApplicationType(WebApplicationType.SERVLET);
		app.run(args);
	}

}
