package com.marco.cloud_ecommerce_api.infrastructure.dataloader;

import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.entity.CategoryJpaEntity;
import com.marco.cloud_ecommerce_api.infrastructure.persistence.jpa.repository.CategoryJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryJpaRepository categoryRepository;

    public DataLoader(CategoryJpaRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String...args) {
        if (categoryRepository.count() == 0) {
            List<CategoryJpaEntity> categories = List.of(
                    new CategoryJpaEntity("Electronics"),
                    new CategoryJpaEntity("Clothing"),
                    new CategoryJpaEntity("Home & Kitchen"),
                    new CategoryJpaEntity("Books"),
                    new CategoryJpaEntity("Sports")
            );
            categoryRepository.saveAll(categories);
            System.out.println("✅ Seed data loaded: " + categories.size() + " categories");
        } else {
            System.out.println("ℹ️ Categories already exist (" + categoryRepository.count() + "). Skipping seed data.");
        }

    }
}
