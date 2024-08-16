package site.leesoyeon.avalanche.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "site.leesoyeon.avalanche.product.client")
public class AvalancheProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheProductApplication.class, args);
    }

}
