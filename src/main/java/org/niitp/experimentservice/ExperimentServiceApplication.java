package org.niitp.experimentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class ExperimentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExperimentServiceApplication.class, args);
    }

}
