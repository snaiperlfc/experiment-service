package org.niitp.experimentservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExperimentItem {

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, message = "Name must not be empty")
    private String name;
    @Size(max = 255, message = "Description should not exceed 255 characters")
    private String description;
    @NotNull(message = "Date and time cannot be null")
//    @Future(message = "Date and time must be in the future")
    @NotNull(message = "Date and time cannot be null")
//    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}[+-]\\d{2}:\\d{2})$", message = "Date time must follow the correct pattern (ISO 8601)")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @JsonProperty("date_time")
    private Date dateTime;
}
