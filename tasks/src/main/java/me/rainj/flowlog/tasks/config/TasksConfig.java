package me.rainj.flowlog.tasks.config;

import lombok.Data;
import me.rainj.flowlog.exceptions.FlowlogException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Validated
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "flowlog")
@Data
public class TasksConfig implements InitializingBean {

    @NotEmpty
    private List<TaskConfig> tasks = new ArrayList<>();

    @NotNull
    private File sparkHome;

    public void validate() {
        if (!this.sparkHome.exists())
            throw new FlowlogException("The spark home: " + this.sparkHome.getAbsolutePath() + " not exists.");
        for (TaskConfig task : this.tasks) {
            task.validate();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.validate();
    }
}

