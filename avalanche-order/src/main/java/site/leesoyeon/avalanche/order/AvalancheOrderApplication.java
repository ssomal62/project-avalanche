package site.leesoyeon.avalanche.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "site.leesoyeon.avalanche.order.infrastructure.external.client")
public class AvalancheOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheOrderApplication.class, args);
    }

}
