package site.leesoyeon.avalanche.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class AvalancheEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvalancheEurekaApplication.class, args);
    }

}
