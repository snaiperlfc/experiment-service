package org.niitp.experimentservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ExperimentService {

    private final ExperimentRepository experimentRepository;

    public List<Experiment> getExperiments() {
        List<Experiment> experiments = experimentRepository.findAll();
        log.info("Fetched all experiments: {}", experiments);
        return experiments;
    }

    public Optional<Experiment> getExperimentById(String id) {
        return experimentRepository.findById(id);
    }

    public Experiment addExperiment(@Valid Experiment experiment) {
//        try {
            Experiment savedExperiment = experimentRepository.save(experiment);
            log.info("Inserted experiment: {}", savedExperiment);
            return savedExperiment;
//        } catch (Exception e) {
//            log.error("Error occurred while inserting experiment: {}", e.getMessage());
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert experiment");
//        }
    }

    public Experiment updateExperiment(@Valid Experiment experiment) {
//        try {
            Experiment updatedExperiment = experimentRepository.save(experiment);
            log.info("Updated experiment: {}", updatedExperiment);
            return updatedExperiment;
//        } catch (Exception e) {
//            log.error("Error occurred while updating experiment: {}", e.getMessage());
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update experiment");
//        }
    }
}
