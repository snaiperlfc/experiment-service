package org.niitp.experimentservice.repository;

import org.niitp.experimentservice.model.Experiment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExperimentRepository extends ReactiveMongoRepository<Experiment, String> {
    Flux<Experiment> findByName(String name);
}
