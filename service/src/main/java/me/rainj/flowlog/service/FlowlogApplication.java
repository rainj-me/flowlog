package me.rainj.flowlog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class FlowlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlowlogApplication.class, args);
	}

}
