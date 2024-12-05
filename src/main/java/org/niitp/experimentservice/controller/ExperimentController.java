package org.niitp.experimentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.model.ExperimentItem;
import org.niitp.experimentservice.service.ExperimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/experiments")
@Tag(name = "info", description = "Предоставление информации об эксперименте")
@RequiredArgsConstructor
public class ExperimentController {

    private final ExperimentService experimentService;

    @Operation(
            summary = "Получить данные",
            description = "Получаем данные"
    )
    @GetMapping
    public List<Experiment> getExperiments() {
        return experimentService.getExperiments();
    }

    @Operation(
            summary = "Добавить данные",
            description = "Добавляем данные"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Experiment addExperiment(@RequestBody Experiment experiment) {
        log.info("Add experiment: {}", experiment);
        return experimentService.addExperiment(experiment);
    }

    @Operation(
            summary = "Получить данные",
            description = "Получаем данные, указав id"
    )
    @GetMapping("/{id}")
    public Experiment getExperimentById(@PathVariable String id) {
        return experimentService.getExperimentById(id);
    }

    @Operation(
            summary = "Обновить точки времени эксперимента",
            description = "Добавляет новые точки времени к существующему эксперименту"
    )
    @PutMapping(value = "/{id}/time_points", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Experiment addTimePoints(@PathVariable String id, @RequestBody List<ExperimentItem> newTimePoints) {
        log.info("Add time points to experiment with ID: {}", id);

        Experiment experiment = experimentService.getExperimentById(id);
        if (experiment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found");
        }

        if (experiment.getTimePoints() != null) {
            experiment.getTimePoints().addAll(newTimePoints);
        } else {
            experiment.setTimePoints(newTimePoints);
        }

        return experimentService.updateExperiment(experiment);
    }
}
