package com.marco.cloud_ecommerce_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@EnableJpaAuditing
public class CloudEcommerceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudEcommerceApiApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable) // Desactiva la protección CSRF para poder hacer POST/PUT luego
				.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll() // ¡Deja pasar a todos sin exigir token!
				);
		return http.build();
	}

}
