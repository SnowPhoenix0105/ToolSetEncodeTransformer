package top.snowphoenix.toolsetencodetransformer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("files")
@Getter
@Setter
public class FileConfig {
    private String workDir;
}
