package top.snowphoenix.toolsetencodetransformer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("redis")
@Getter
@Setter
public class RedisConfig {
    private long timeoutMinute;
}
