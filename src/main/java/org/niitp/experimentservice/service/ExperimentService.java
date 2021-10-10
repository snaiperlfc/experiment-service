package org.niitp.experimentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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

    public Boolean addExperiment(Experiment experiment) {
        experimentRepository.insert(experiment).subscribe(experiment1 -> log.info(experiment1.toString()));
        return true;
    }
}
