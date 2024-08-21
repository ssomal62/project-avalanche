package site.leesoyeon.avalanche.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "site.leesoyeon.avalanche.user.infrastructure.external.client")
public class AvalancheUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheUserApplication.class, args);
    }

}
