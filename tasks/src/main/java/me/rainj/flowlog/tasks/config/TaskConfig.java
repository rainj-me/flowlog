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

@Data
@Validated
public class TaskConfig {
    @NotEmpty
    private String name;
    @NotNull
    private TaskType type;
    @NotNull
    private File jarFile;
    @NotEmpty
    private String mainClass;

    private String executorCores = "1";

    private String executorMem = "512M";

    private List<String> packages = new ArrayList<>();

    private List<File> dependencies = new ArrayList<>();

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
