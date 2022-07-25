package me.rainj.flowlog.tasks.config;

import lombok.Data;
import me.rainj.flowlog.exceptions.FlowlogException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The scheduling task config.
 */
@Data
@Validated
public class TaskConfig {
    /**
     * Task name.
     */
    @NotEmpty
    private String name;
    /**
     * Task type.
     */
    @NotNull
    private TaskType type;
    /**
     * Job jar file.
     */
    @NotNull
    private File jarFile;
    /**
     * Job main class.
     */
    @NotEmpty
    private String mainClass;
    /**
     * Number of CPU core for executor.
     */
    private String executorCores = "1";
    /**
     * Memory size for executor.
     */
    private String executorMem = "512M";
    /**
     * Spark job additional package config, can be empty.
     */
    private List<String> packages = new ArrayList<>();
    /**
     * Job jar file dependencies, can be empty.
     */
    private List<File> dependencies = new ArrayList<>();

    /**
     * Validate whether the task config is valid.
     */
    public void validate() {
        if (!this.jarFile.exists())
            throw new FlowlogException("The jar file: " + this.jarFile.getAbsolutePath() + " not exists.");
        if (!CollectionUtils.isEmpty(this.dependencies)) {
            for (File file : this.dependencies) {
                if (!file.exists())
                    throw new FlowlogException("The dependency file: " + file.getAbsolutePath() + " not exists.");
            }
        }
    }
}
