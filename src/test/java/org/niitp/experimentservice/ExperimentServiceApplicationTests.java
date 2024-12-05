package org.niitp.experimentservice;

import org.junit.jupiter.api.*;
import org.niitp.experimentservice.model.Experiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ExperimentServiceApplicationTests {

    private static Experiment createdExperiment;

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

    @Test
    @Order(1)
    public void addExperiment() throws Exception {
        String experimentJson = """
                {
                    "name": "work1",
                    "description": "Some experiment description",
                    "date_time_start": "2024-11-27T02:25:11.000+03:00",
                    "date_time_finish": "2024-11-27T02:25:14.000+03:00",
                    "time_points": [
                        {
                            "name": "point 1",
                            "description": "started the process",
                            "date_time": "2024-11-27T02:25:12.000+03:00"
                        },
                        {
                            "name": "point 2",
                            "description": "ended the process",
                            "date_time": "2024-11-27T02:25:13.000+03:00"
                        }
                    ]
                }
                """;

        // Perform the POST request
        String createdExperimentString = mockMvc.perform(post("/experiments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(experimentJson))
                .andExpect(status().isCreated()) // Status should be 201 Created
                .andExpect(jsonPath("$.name").value("work1")) // Verifying that the name field in the response is "work1"
                .andExpect(jsonPath("$.description").value("Some experiment description")) // Verifying description
                .andExpect(jsonPath("$.time_points.length()").value(2)) // Verifying the number of time points
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        createdExperiment = objectMapper.readValue(createdExperimentString, Experiment.class);
        assertNotNull(createdExperiment.getId());
    }

    @Test
    @Order(2)
    public void getExperimentById() throws Exception {
        mockMvc.perform(get("/experiments/{id}", createdExperiment.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(createdExperiment.getId()));
    }


    @Test
    @Order(3)
    public void getExperiments() throws Exception {
        // Perform the GET request to /experiments
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/experiments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()) // Assert status code 200
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1)) // Assert the list length
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("work1")) // Assert the first experiment's name
                .andReturn();

        // Optionally, you can print out the response for debugging
        String responseContent = result.getResponse().getContentAsString();
        System.out.println(responseContent);
    }


    @Test
    @Order(4)
    public void updateTimePoints() throws Exception {
        String newTimePoints = """
                [
                    {
                        "name": "point 3",
                        "description": "started another process",
                        "date_time": "2024-11-27T02:30:00.000+03:00"
                    },
                    {
                        "name": "point 4",
                        "description": "ended another process",
                        "date_time": "2024-11-27T02:35:00.000+03:00"
                    }
                ]
                """;

        mockMvc.perform(put("/experiments/{id}/time_points", createdExperiment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTimePoints))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.time_points.length()").value(4))  // Verifying the size of timePoints array
                .andExpect(jsonPath("$.time_points[2].name").value("point 3"))  // Verifying the newly added time point
                .andExpect(jsonPath("$.time_points[3].name").value("point 4"));  // Verifying the newly added time point
    }


}

