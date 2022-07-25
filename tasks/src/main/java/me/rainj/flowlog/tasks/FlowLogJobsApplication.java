package me.rainj.flowlog.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Flow log spark jobs applciation.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FlowLogJobsApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(FlowLogJobsApplication.class);
    }

}