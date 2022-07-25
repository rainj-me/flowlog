package me.rainj.flowlog.tasks.schedulers;

import me.rainj.flowlog.tasks.config.TaskConfig;
import me.rainj.flowlog.tasks.config.TaskType;
import me.rainj.flowlog.tasks.config.TasksConfig;
import me.rainj.flowlog.tasks.runner.SparkTaskRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Schedulers.
 */
@Component
public class Schedulers {
    /**
     * Spark tasks configs.
     */
    @Autowired
    private TasksConfig config;

    /**
     * Run every 5 minutes from the 2nd minutes. (e.g. schedule at 00:07:01, 00:12:01 etc.)
     */
    @Async
    @Scheduled(cron = "1 2/5 * * * *")
    public void runEveryFiveMinutes() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.FIVE_MINUTES)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }

    /**
     * Run every hour from the 6th minutes. (e.g. schedule at 01:06:01, 02:06:01 etc.)
     */
    @Async
    @Scheduled(cron = "1 6 * * * *")
    public void runEveryOneHour() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.ONE_HOUR)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }

    /**
     * Run every day from the 2nd hours. (e.g. schedule at 2020-01-01 02:06:01, 2020-01-02 02:06:01 etc.)
     */
    @Async
    @Scheduled(cron = "1 6 2 * * *")
    public void runEveryOneDay() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.ONE_DAY)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }
}
