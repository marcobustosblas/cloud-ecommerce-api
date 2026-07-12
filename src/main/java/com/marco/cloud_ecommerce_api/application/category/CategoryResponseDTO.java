package com.marco.cloud_ecommerce_api.application.category;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CategoryResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime created;
    private LocalDateTime updated;

    public CategoryResponseDTO(UUID id, String name, String description, boolean active,
                               LocalDateTime created, LocalDateTime updated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.created = created;
        this.updated = updated;
    }

}
