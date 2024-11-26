package org.niitp.experimentservice;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.niitp.experimentservice.model.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
class ExperimentServiceApplicationTests {

    private static Experiment createdExperiment;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer();

    @DynamicPropertySource
    static void setMongoDBProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private WebTestClient webTestClient;

    @LocalServerPort
    private int localServerPort;

    @BeforeAll
    static void startContainers() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        mongoDBContainer.stop();
    }

    @Test
    public void addExperiment() throws Exception {
        String experiment = """
                {
                   "name": "work1",
                   "description": "некие работы",
                   "date_time_start": "2024-11-27T02:25:11.000+03:00",
                   "date_time_finish": "2024-11-27T02:25:14.000+03:00",
                   "time_points": [
                     {
                       "name": "point 1",
                       "description": "включили насос",
                       "date_time": "2024-11-27T02:25:12.000+03:00"
                     },
                     {
                       "name": "point 2",
                       "description": "выключили насос",
                       "date_time": "2024-11-27T02:25:13.000+03:00"
                     }
                   ]
                 }""";

        createdExperiment = webTestClient.post()
                .uri("/experiments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(experiment)
                .exchange()
                .expectStatus().isCreated()
                .returnResult(Experiment.class)
                .getResponseBody()
                .blockFirst();
    }

    @Test
    public void getExperimentById() throws Exception {

        webTestClient.get()
                .uri("/experiments/{id}", createdExperiment.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testIndexStreaming() {
        Flux<Experiment> experimentFlux = webTestClient.get()
                .uri("/experiments")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Experiment.class)
                .getResponseBody();

        StepVerifier.create(experimentFlux)
                .thenRequest(1)
                .expectNextCount(1) // Adjust based on expected number of elements
                .thenAwait(Duration.ofSeconds(5)) // Increase this duration if necessary
                .expectNextCount(1) // Additional verification or adjust count
                .thenCancel()
                .verify(Duration.ofSeconds(10)); // Adjust main timeout duration
    }
}

