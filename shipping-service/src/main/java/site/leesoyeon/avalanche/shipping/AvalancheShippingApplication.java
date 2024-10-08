package site.leesoyeon.avalanche.shipping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableDiscoveryClient
@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients(basePackages = "site.leesoyeon.avalanche.shipping.infrastructure.external.client")
public class AvalancheShippingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheShippingApplication.class, args);
    }
}
