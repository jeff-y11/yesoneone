package com.geodevai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
@RequiredArgsConstructor
public class DataConfiguration implements RepositoryRestConfigurer {
    private final EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream()
                .map(Type::getJavaType)
                .toArray(Class[]::new));
    }
}
