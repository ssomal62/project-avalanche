package site.leesoyeon.avalanche.point;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "site.leesoyeon.avalanche.point.client")
public class AvalanchePointApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalanchePointApplication.class, args);
    }

}
