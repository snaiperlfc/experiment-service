package org.niitp.experimentservice.model;

import java.util.Date;

public record ErrorDetails(Date timestamp, String message, String details) {
}
