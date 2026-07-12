package com.marco.cloud_ecommerce_api.infrastructure.dataloader;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryJpaRepository categoryRepository;

    public DataLoader(CategoryJpaRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();

            // Uso el constructor completo (All-Args) con IDs autogenerados y descripciones reales
            List<CategoryJpaEntity> categories = List.of(
                    new CategoryJpaEntity(UUID.randomUUID(), "Electronics", "Laptops, smartphones, and tech accessories", true, now, now),
                    new CategoryJpaEntity(UUID.randomUUID(), "Clothing", "Fashionable apparel for men, women, and kids", true, now, now),
                    new CategoryJpaEntity(UUID.randomUUID(), "Home & Kitchen", "Furniture, appliances, and home decor items", true, now, now),
                    new CategoryJpaEntity(UUID.randomUUID(), "Books", "Physical novels, e-books, and educational material", true, now, now),
                    new CategoryJpaEntity(UUID.randomUUID(), "Sports", "Fitness gear, outdoor equipment, and sportswear", true, now, now)
            );

            categoryRepository.saveAll(categories);
            System.out.println("✅ Seed data loaded: " + categories.size() + " categories with descriptions.");
        } else {
            System.out.println("ℹ️ Categories already exist (" + categoryRepository.count() + "). Skipping seed data.");
        }
    }
}