package org.niitp.experimentservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.model.ExperimentItem;
import org.niitp.experimentservice.model.ResourceNotFoundException;
import org.niitp.experimentservice.service.ExperimentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@CrossOrigin(maxAge = 3600)
@RestController
@Slf4j
@RequestMapping("/experiments")
@Tag(name = "info", description = "Предоставление информации об эксперименте")
@RequiredArgsConstructor
@Validated
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
    public ResponseEntity<Experiment> addExperiment(@Valid @RequestBody Experiment experiment) {
        log.info("Add experiment: {}", experiment);

        // Save the experiment (you can adjust this as needed based on your service method)
        Experiment savedExperiment = experimentService.addExperiment(experiment);

        // Get the location URL (assuming the experiment has an 'id' that can be used to build the location URI)
        String location = "/experiments/" + savedExperiment.getId();

        // Return the response with status CREATED (201) and the location header
        return ResponseEntity.created(URI.create(location)).body(savedExperiment);
    }

    @Operation(
            summary = "Обновить данные эксперимента",
            description = "Обновляет данные существующего эксперимента"
    )
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Experiment updateExperiment(@PathVariable String id, @Valid @RequestBody Experiment updatedExperiment) throws ResourceNotFoundException {
        log.info("Update experiment with ID: {}", id);
        return experimentService.getExperimentById(id)
                .map(existingExperiment -> {
                    // Update fields of the existing experiment
                    existingExperiment.setName(updatedExperiment.getName());
                    existingExperiment.setDescription(updatedExperiment.getDescription());
                    existingExperiment.setDate_time_start(updatedExperiment.getDate_time_start());
                    existingExperiment.setDateTimeFinish(updatedExperiment.getDateTimeFinish());
                    existingExperiment.setTimePoints(updatedExperiment.getTimePoints());
                    return existingExperiment;
                })
                .map(experimentService::updateExperiment)
                .orElseThrow(() -> new ResourceNotFoundException("Experiment not found for this id :: " + id));
    }


    @Operation(
            summary = "Получить данные",
            description = "Получаем данные, указав id"
    )
    @GetMapping("/{id}")
    public Experiment getExperimentById(@PathVariable String id) {
        return experimentService.getExperimentById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found"));
    }

    @Operation(
            summary = "Обновить точки времени эксперимента",
            description = "Добавляет новые точки времени к существующему эксперименту"
    )
    @PutMapping(value = "/{id}/time_points", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Experiment addTimePoints(@PathVariable String id, @Valid @RequestBody List<ExperimentItem> newTimePoints) {
        log.info("Add time points to experiment with ID: {}", id);

        Experiment experiment = experimentService.getExperimentById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found")
        );

        if (experiment.getTimePoints() != null) {
            experiment.getTimePoints().addAll(newTimePoints);
        } else {
            experiment.setTimePoints(newTimePoints);
        }

        return experimentService.updateExperiment(experiment);
    }
}
