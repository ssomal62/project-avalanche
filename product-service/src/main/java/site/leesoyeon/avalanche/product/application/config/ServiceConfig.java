package site.leesoyeon.avalanche.product.application.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "avalanche-config")
@Getter
@Setter
public class ServiceConfig {
    private String property;
}
