package org.niitp.experimentservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.service.ExperimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequestMapping("/experiment")
public class ExperimentController {

    final
    ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Experiment> index() {
        return experimentService.getExperiments();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Experiment> addExperiment(@RequestBody Experiment experiment) {
        log.info("Add experiment: {}", experiment);
        return experimentService.addExperiment(experiment);
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Experiment> getExperimentById(@PathVariable String id) {
        return experimentService.getExperimentById(id);
    }

}
