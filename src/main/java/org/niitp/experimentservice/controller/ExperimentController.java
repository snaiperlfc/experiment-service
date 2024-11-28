package org.niitp.experimentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/experiments")
@Tag(name = "info", description = "Предоставление информации об эксперименте")
public class ExperimentController {

    final
    ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @Operation(
            summary = "Получить данные",
            description = "Получаем данные")
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Experiment> index() {
        return experimentService.getExperiments();
    }

    @Operation(
            summary = "Добавить данные",
            description = "Добавляем данные")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Experiment> addExperiment(@RequestBody Experiment experiment) {
        log.info("Add experiment: {}", experiment);
        return experimentService.addExperiment(experiment);
    }

    @Operation(
            summary = "Получить данные",
            description = "Получаем данные, указав id")
    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Experiment> getExperimentById(@PathVariable String id) {
        return experimentService.getExperimentById(id);
    }

}
