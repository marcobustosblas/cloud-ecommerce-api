package com.marco.cloud_ecommerce_api.domain.model;

import java.util.UUID;

public class Category {
    private UUID id;
    private String name;

    public Category(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {return this.id;}
    public String getName() {return this.name;}
}
