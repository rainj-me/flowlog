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

@Component
public class Schedulers {
    @Autowired
    private TasksConfig config;

    @Async
    @Scheduled(cron = "1 0 * * * *")
    public void runEveryOneHour() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.ONE_HOUR)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }

    @Async
    @Scheduled(cron = "1 0/5 * * * *")
    public void runEveryFiveMinutes() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.FIVE_MINUTES)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }

    @Async
    @Scheduled(cron = "1 0 0 * * *")
    public void runEveryOneDay() {
        List<TaskConfig> tasks = config.getTasks().stream()
                .filter(task -> task.getType() == TaskType.ONE_DAY)
                .collect(Collectors.toList());
        tasks.forEach(task -> {
            new SparkTaskRunner(task, config.getSparkHome()).run();
        });
    }
}
