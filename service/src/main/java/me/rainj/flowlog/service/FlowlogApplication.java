package me.rainj.flowlog.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * FlowlogApplication, this spring boot webflux service is used to process and aggregate the flow logs report by agent
 * on production instances.
 */
@SpringBootApplication
@EnableWebFlux
@EnableScheduling
public class FlowlogApplication {

	/**
	 * Main method.
	 * @param args commandline arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(FlowlogApplication.class, args);
	}
}
