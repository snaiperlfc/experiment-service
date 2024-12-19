package org.niitp.experimentservice;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.niitp.experimentservice.controller.ExperimentController;
import org.niitp.experimentservice.model.Experiment;
import org.niitp.experimentservice.model.ExperimentItem;
import org.niitp.experimentservice.model.ResourceNotFoundException;
import org.niitp.experimentservice.service.ExperimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ExperimentServiceApplicationTests {

    private ExperimentService experimentService;
    private ExperimentController experimentController;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer();

    @DynamicPropertySource
    static void setMongoDBProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void startContainers() {
        mongoDBContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        mongoDBContainer.stop();
    }

    @BeforeEach
    void setUp() {
        experimentService = Mockito.mock(ExperimentService.class);
        experimentController = new ExperimentController(experimentService);
        mockMvc = MockMvcBuilders.standaloneSetup(experimentController).build();
    }


    @Test
    void addExperiment_success() throws Exception {
        Experiment newExperiment = new Experiment(
                null, // No ID for new experiments
                "Test Experiment",
                "A description of the test experiment",
                new Date(),
                new Date(),
                Collections.emptyList()
        );

        Experiment savedExperiment = new Experiment(
                "123", // ID assigned after saving
                "Test Experiment",
                "A description of the test experiment",
                newExperiment.getDate_time_start(),
                newExperiment.getDateTimeFinish(),
                newExperiment.getTimePoints()
        );

        when(experimentService.addExperiment(any(Experiment.class))).thenReturn(savedExperiment);

        mockMvc.perform(post("/experiments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Test Experiment",
                                    "description": "A description of the test experiment",
                                    "date_time_start": "2024-11-28T10:00:00.000+03:00",
                                    "date_time_finish": "2024-11-28T18:00:00.000+03:00",
                                    "time_points": []
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.name", is("Test Experiment")))
                .andExpect(jsonPath("$.description", is("A description of the test experiment")));

        verify(experimentService, times(1)).addExperiment(any(Experiment.class));
    }

//    @Test
//    void addExperiment_failure() throws Exception {
//
//        when(experimentService.addExperiment(any(Experiment.class))).thenThrow(new RuntimeException());
//
//        mockMvc.perform(post("/experiments")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "name": "",
//                                    "description": "A description of the test experiment",
//                                    "date_time_start": "2024-11-28T10:00:00.000+03:00",
//                                    "date_time_finish": "2024-11-28T18:00:00.000+03:00",
//                                    "time_points": []
//                                }
//                                """))
//                .andExpect(status().isBadRequest());
//
//        verify(experimentService, times(1)).addExperiment(any(Experiment.class));
//    }

    @Test
    void getExperimentById_success() throws Exception {
        String experimentId = "123";
        Experiment experiment = new Experiment(
                experimentId,
                "Test Experiment",
                "A description of the test experiment",
                new Date(),
                new Date(),
                Collections.emptyList()
        );

        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.of(experiment));

        mockMvc.perform(get("/experiments/{id}", experimentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.name", is("Test Experiment")))
                .andExpect(jsonPath("$.description", is("A description of the test experiment")));

        verify(experimentService, times(1)).getExperimentById(experimentId);
    }

    @Test
    void getExperimentById_notFound() throws Exception {
        String experimentId = "123";

        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/experiments/{id}", experimentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
//                .andExpect(result -> result.getResolvedException() instanceof ResponseStatusException)
                .andExpect(result -> ((ResponseStatusException) result.getResolvedException()).getReason().equals("Experiment not found"));

        verify(experimentService, times(1)).getExperimentById(experimentId);
    }


    @Test
    void getExperiments_success() throws Exception {
        Experiment experiment1 = new Experiment(
                "1",
                "Experiment 1",
                "Description 1",
                new Date(),
                new Date(),
                null
        );
        Experiment experiment2 = new Experiment(
                "2",
                "Experiment 2",
                "Description 2",
                new Date(),
                new Date(),
                null
        );

        when(experimentService.getExperiments()).thenReturn(Arrays.asList(experiment1, experiment2));

        mockMvc.perform(get("/experiments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Experiment 1")))
                .andExpect(jsonPath("$[0].description", is("Description 1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].name", is("Experiment 2")))
                .andExpect(jsonPath("$[1].description", is("Description 2")));

        verify(experimentService, times(1)).getExperiments();
    }

    @Test
    void getExperiments_emptyList() throws Exception {
        when(experimentService.getExperiments()).thenReturn(List.of());

        mockMvc.perform(get("/experiments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(experimentService, times(1)).getExperiments();
    }

    @Test
    void updateTimePoints_success() throws Exception {
        // Experiment and new time points setup
        String experimentId = "123";
        ExperimentItem newTimePoint1 = new ExperimentItem("Point 3", "Description 3", new Date());
        ExperimentItem newTimePoint2 = new ExperimentItem("Point 4", "Description 4", new Date());

        // Create an existing experiment with two time points
        Experiment existingExperiment = new Experiment(
                experimentId,
                "Test Experiment",
                "A description of the test experiment",
                new Date(),
                new Date(),
                null
        );

        // Updated experiment (after adding new time points)
        Experiment updatedExperiment = new Experiment(
                experimentId,
                "Test Experiment",
                "A description of the test experiment",
                existingExperiment.getDate_time_start(),
                existingExperiment.getDateTimeFinish(),
                Arrays.asList(
                        newTimePoint1,
                        newTimePoint2
                )
        );

        // Mock service methods
        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.of(existingExperiment));
        when(experimentService.updateExperiment(any(Experiment.class))).thenReturn(updatedExperiment);

        // Perform the PUT request to update the time points
        mockMvc.perform(put("/experiments/{id}/time_points", experimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {"name": "Point 3", "description": "Description 3", "date_time": "2024-11-28T12:00:00.000+03:00"},
                                    {"name": "Point 4", "description": "Description 4", "date_time": "2024-11-28T13:00:00.000+03:00"}
                                ]
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.time_points", hasSize(2)))
                .andExpect(jsonPath("$.time_points[0].name", is("Point 3")))
                .andExpect(jsonPath("$.time_points[1].name", is("Point 4")));

        // Verify that the service methods were called as expected
        verify(experimentService, times(1)).getExperimentById(experimentId);
        verify(experimentService, times(1)).updateExperiment(any(Experiment.class));
    }

    @Test
    void updateTimePoints_notFound() throws Exception {
        String experimentId = "123";

        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.empty());

        mockMvc.perform(put("/experiments/{id}/time_points", experimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                    {"name": "Point 3", "description": "Description 3", "date_time": "2024-11-28T12:00:00.000+03:00"},
                                    {"name": "Point 4", "description": "Description 4", "date_time": "2024-11-28T13:00:00.000+03:00"}
                                ]
                                """))
                .andExpect(status().isNotFound())
//                .andExpect(result -> result.getResolvedException() instanceof ResponseStatusException)
                .andExpect(result -> ((ResponseStatusException) result.getResolvedException()).getReason().equals("Experiment not found"));

        verify(experimentService, times(1)).getExperimentById(experimentId);
        verify(experimentService, times(0)).updateExperiment(any(Experiment.class));
    }

    @Test
    void updateExperiment_success() throws Exception {
        String experimentId = "123";
        Experiment existingExperiment = new Experiment(
                experimentId, "Old Name", "Old Description", new Date(), new Date(), Collections.emptyList()
        );

        Experiment updatedExperiment = new Experiment(
                experimentId, "Updated Name", "Updated Description", new Date(), new Date(), Collections.emptyList()
        );

        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.of(existingExperiment));
        when(experimentService.updateExperiment(any(Experiment.class))).thenReturn(updatedExperiment);

        mockMvc.perform(put("/experiments/{id}", experimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "123",
                                    "name": "Updated Name",
                                    "description": "Updated Description",
                                    "date_time_start": "2024-11-28T10:00:00.000+03:00",
                                    "date_time_finish": "2024-11-28T18:00:00.000+03:00",
                                    "time_points": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("123")))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")));

        verify(experimentService, times(1)).getExperimentById(experimentId);
        verify(experimentService, times(1)).updateExperiment(any(Experiment.class));
    }

    @Test
    void updateExperiment_notFound() throws Exception {
        String experimentId = "123";

        when(experimentService.getExperimentById(eq(experimentId))).thenReturn(Optional.empty());

        mockMvc.perform(put("/experiments/{id}", experimentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "123",
                                    "name": "Updated Name",
                                    "description": "Updated Description",
                                    "date_time_start": "2024-11-28T10:00:00.000+03:00",
                                    "date_time_finish": "2024-11-28T18:00:00.000+03:00",
                                    "time_points": []
                                }
                                """))
                .andExpect(status().isNotFound())
//                .andExpect(result -> result.getResolvedException() instanceof ResponseStatusException)
                .andExpect(result -> ((ResourceNotFoundException) result.getResolvedException()).getMessage().equals("Experiment not found"));

        verify(experimentService, times(1)).getExperimentById(experimentId);
        verify(experimentService, times(0)).updateExperiment(any(Experiment.class));
    }

}

