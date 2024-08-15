package site.leesoyeon.probabilityrewardsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProjectAvalancheApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectAvalancheApplication.class, args);
    }

}
