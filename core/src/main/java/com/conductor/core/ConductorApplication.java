package com.conductor.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication(scanBasePackages = {
        "com.conductor.core",
        "com.conductor.adapters"
})
public class ConductorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConductorApplication.class, args);
    }
}
