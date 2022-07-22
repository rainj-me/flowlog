package me.rainj.flowlog.scheduler.tasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class StreamFromKafkaTask {
    @Value(value = "${spark.stream_from_kakfka.jar")
    private String jarFilePath;

    @Value(value = "${spark.stream_from_kafka.job_class}" )
    private String jobClass;

    @Scheduled(fixedRate = 1000 * 24 * 60, timeUnit = TimeUnit.SECONDS, initialDelay = 1)
    public void runEvery3y() {

    }

}
