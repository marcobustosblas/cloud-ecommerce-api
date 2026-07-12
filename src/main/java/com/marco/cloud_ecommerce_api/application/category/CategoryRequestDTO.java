package com.marco.cloud_ecommerce_api.application.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDTO {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Category description is required")
    @Size(min = 10, max = 255, message = "Category description must be between 10 and 255 characters")
    private String description;

}
