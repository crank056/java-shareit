package ru.practicum.shareit.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({"ru.practicum.shareit"})
@EntityScan(basePackages = "ru.practicum.shareit")
@EnableJpaRepositories(basePackages = "ru.practicum.shareit")
@PropertySource("classpath:/application.properties")
public class DataConfig {
}
