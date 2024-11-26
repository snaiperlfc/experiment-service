package org.niitp.experimentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.stereotype.Service;
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

    public Flux<Experiment> getExperimentById(String id) {
        return Flux
                .interval(Duration.ZERO, Duration.ofSeconds(5))
                .flatMap(i -> experimentRepository.findById(id));
    }

    public Mono<Experiment> addExperiment(Experiment experiment) {
        return experimentRepository.insert(experiment)
                .doOnSuccess(experiment1 -> log.info(experiment1.toString()))
                .onErrorResume(e -> {
                    log.error("Error occurred while inserting experiment: {}", e.getMessage());
                    return Mono.error(e);
                });
    }

}
