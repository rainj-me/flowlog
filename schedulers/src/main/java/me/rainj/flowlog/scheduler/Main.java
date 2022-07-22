package me.rainj.flowlog.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) throws InterruptedException {

        SpringApplication.run(Main.class);
        //Thread.sleep(100 * 1000L);
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void runTask() throws IOException {
        File resource = new ClassPathResource("jobs/jobs.jar").getFile();
        System.out.println("resource: " + resource.getAbsolutePath());

    }

}