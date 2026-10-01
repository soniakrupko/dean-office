package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class BeanConfig {

    private final AtomicInteger counter = new AtomicInteger();

    @Bean
    @Scope("singleton")
    public DemoBean singletonBean() {
        return new DemoBean(counter.incrementAndGet());
    }

    @Bean
    @Scope("prototype")
    public DemoBean prototypeBean() {
        return new DemoBean(counter.incrementAndGet());
    }
}