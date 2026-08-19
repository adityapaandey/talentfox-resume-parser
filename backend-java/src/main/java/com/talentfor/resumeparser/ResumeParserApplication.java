package com.talentfor.resumeparser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Resume Parser Application - Spring Boot Main Class
 * No LLM API Required - Pattern Matching Only
 */
@SpringBootApplication
public class ResumeParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeParserApplication.class, args);
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Resume Parser API is running!");
        System.out.println("Backend URL: http://localhost:8080");
        System.out.println("API Docs: http://localhost:8080/swagger-ui.html");
        System.out.println("Health: http://localhost:8080/api/resume-parser/health");
        System.out.println("=".repeat(50) + "\n");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:8080",
                                "http://127.0.0.1:3000",
                                "http://127.0.0.1:8080"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
