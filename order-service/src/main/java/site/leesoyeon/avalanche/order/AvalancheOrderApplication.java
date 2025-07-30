package site.leesoyeon.avalanche.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class AvalancheOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheOrderApplication.class, args);
    }

}
