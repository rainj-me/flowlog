package me.rainj.flowlog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * FlowlogApplication, this spring boot webflux service is used to process and aggregate the flow logs report by agent
 * on production instances.
 */
@SpringBootApplication
@EnableWebFlux
public class FlowlogApplication {

	/**
	 * Main method.
	 * @param args commandline arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(FlowlogApplication.class, args);
	}

}
