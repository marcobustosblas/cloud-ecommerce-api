package com.marco.cloud_ecommerce_api.domain.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s con %s '%s' no fue encontrado.", resource, field, value));
    }
}
