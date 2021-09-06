package top.snowphoenix.toolsetencodetransformer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Getter
@Setter
public class JwtConfig {
    private String publicKeyPath;
}
