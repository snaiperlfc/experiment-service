package org.niitp.experimentservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/experiment")
public class ExperimentController {

    @Autowired
    private ExperimentRepository experimentRepository;

    @GetMapping
    public Flux<Experiment> index() {
        return experimentRepository.findAll();
    }

    @PostMapping
    public Mono<Experiment> addExperiment(@RequestBody Experiment experiment) {
        return experimentRepository.insert(experiment).doOnSuccess(experiment1 -> log.info(experiment1.toString()));
    }

}
