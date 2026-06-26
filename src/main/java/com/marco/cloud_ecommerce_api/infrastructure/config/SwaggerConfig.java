package com.marco.cloud_ecommerce_api.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AICES - AI-Commerce-Engine-Software")
                        .version("1.0.0")
                        .description("API REST modular de alto rendimiento para e-commerce cloud, equipada con paginación dinámica, filtros avanzados y manejo global de excepciones.")
                        .contact(new Contact()
                                .name("Marco")
                                .email("my-correo@ejemplo.com")
                                .url("https://github.com/marcobustosblas")
                        )
                );
    }

}
