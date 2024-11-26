package org.niitp.experimentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableReactiveMongoRepositories
@EnableAsync
public class ExperimentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentServiceApplication.class, args);
    }

}
