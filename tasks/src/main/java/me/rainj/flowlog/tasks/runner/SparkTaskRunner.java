package me.rainj.flowlog.tasks.runner;

import lombok.RequiredArgsConstructor;
import me.rainj.flowlog.exceptions.FlowlogException;
import me.rainj.flowlog.tasks.config.TaskConfig;
import org.apache.spark.launcher.SparkLauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class SparkTaskRunner implements Runnable {

    private final TaskConfig task;
    private final File sparkHome;

    @Override
    public void run() {
        SparkLauncher taskLauncher = buildSparkLauncher();

        try {
            Process process = taskLauncher.launch();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            reader.lines().forEach(System.out::println);
            int exitCode = process.waitFor();
            System.out.println("job: " + task.getName() + " exit: " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw new FlowlogException("launch job failed with message: " + e.getMessage());
        }
    }

    private SparkLauncher buildSparkLauncher() {
        SparkLauncher taskLauncher = new SparkLauncher()
                .setSparkHome(sparkHome.getAbsolutePath())
                .setAppName(task.getName())
                .setConf(SparkLauncher.EXECUTOR_CORES, task.getExecutorCores())
                .setConf(SparkLauncher.EXECUTOR_MEMORY, task.getExecutorMem())
                .setAppResource(task.getJarFile().getAbsolutePath())
                .setMainClass(task.getMainClass());

        task.getPackages().stream().reduce((a, b) -> a + "," + b).ifPresent((packages) -> {
            taskLauncher.addSparkArg("--packages", packages);
        });

        task.getDependencies().forEach(file -> {
            taskLauncher.addJar(file.getAbsolutePath());
        });
        return taskLauncher;
    }
}
