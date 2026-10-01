package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public String applicationName() {
        return "Деканат";
    }

    @Bean
    public String applicationVersion() {
        return "Лабораторна робота №2";
    }
}