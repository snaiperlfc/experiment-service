package org.niitp.experimentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class ExperimentService {

    private final ExperimentRepository experimentRepository;

    public ExperimentService(ExperimentRepository experimentRepository) {
        this.experimentRepository = experimentRepository;
    }

    public Flux<Experiment> getExperiments() {
        return Flux
                .interval(Duration.ZERO, Duration.ofSeconds(5))
                .flatMap(i -> experimentRepository.findAll());
    }

    public Mono<Experiment> getExperimentById(String id) {
        return experimentRepository.findById(id)
                .doOnSuccess(exp -> log.info("Fetched experiment by ID: {}", id))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found")));
    }

    public Mono<Experiment> addExperiment(Experiment experiment) {
        return experimentRepository.insert(experiment)
                .doOnSuccess(experiment1 -> log.info(experiment1.toString()))
                .onErrorResume(e -> {
                    log.error("Error occurred while inserting experiment: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<Experiment> updateExperiment(Experiment experiment) {
        return experimentRepository.save(experiment)
                .doOnSuccess(updatedExperiment -> log.info("Updated experiment: {}", updatedExperiment))
                .onErrorResume(e -> {
                    log.error("Error occurred while updating experiment: {}", e.getMessage());
                    return Mono.error(e);
                });
    }


}
