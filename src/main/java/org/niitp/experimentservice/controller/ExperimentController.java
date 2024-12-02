package org.niitp.experimentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.model.ExperimentItem;
import org.niitp.experimentservice.service.ExperimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    @GetMapping(value = "/{id}")
    public Mono<Experiment> getExperimentById(@PathVariable String id) {
        return experimentService.getExperimentById(id);
    }

    @Operation(
            summary = "Обновить точки времени эксперимента",
            description = "Добавляет новые точки времени к существующему эксперименту"
    )
    @PutMapping(value = "/{id}/time_points", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Experiment> addTimePoints(@PathVariable String id, @RequestBody List<ExperimentItem> newTimePoints) {
        log.info("Add time points to experiment with ID: {}", id);
        return experimentService.getExperimentById(id)
                .flatMap(experiment -> {
                    if (experiment.getTimePoints() != null) {
                        experiment.getTimePoints().addAll(newTimePoints);
                    } else {
                        experiment.setTimePoints(newTimePoints);
                    }
                    return experimentService.updateExperiment(experiment);
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found")));
    }


}
