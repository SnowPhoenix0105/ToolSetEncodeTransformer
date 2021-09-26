package top.snowphoenix.toolsetencodetransformer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("charset.file-path")
@Getter
@Setter
public class CharSetConfig {
    private String common3500;
    private String common7000;
}
