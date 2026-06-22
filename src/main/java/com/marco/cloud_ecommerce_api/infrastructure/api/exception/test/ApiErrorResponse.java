package com.marco.cloud_ecommerce_api.infrastructure.api.exception.test;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Map<String, String> details;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = null;
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message, String path, Map<String, String> details) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
