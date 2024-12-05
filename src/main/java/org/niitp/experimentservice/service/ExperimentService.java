package org.niitp.experimentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.repository.ExperimentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExperimentService {

    private final ExperimentRepository experimentRepository;

    public List<Experiment> getExperiments() {
        // Blocking call to find all experiments
        List<Experiment> experiments = experimentRepository.findAll();
        log.info("Fetched all experiments: {}", experiments);
        return experiments;
    }

    public Experiment getExperimentById(String id) {
        // Blocking call to find experiment by ID
        return experimentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Experiment not found with ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found");
                });
    }

    public Experiment addExperiment(Experiment experiment) {
        try {
            Experiment savedExperiment = experimentRepository.save(experiment);
            log.info("Inserted experiment: {}", savedExperiment);
            return savedExperiment;
        } catch (Exception e) {
            log.error("Error occurred while inserting experiment: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to insert experiment");
        }
    }

    public Experiment updateExperiment(Experiment experiment) {
        try {
            Experiment updatedExperiment = experimentRepository.save(experiment);
            log.info("Updated experiment: {}", updatedExperiment);
            return updatedExperiment;
        } catch (Exception e) {
            log.error("Error occurred while updating experiment: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update experiment");
        }
    }
}
