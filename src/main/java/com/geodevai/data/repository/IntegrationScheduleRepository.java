package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.IntegrationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "integration-schedule")
public interface IntegrationScheduleRepository extends JpaRepository<IntegrationSchedule, UUID> {

    List<IntegrationSchedule> findByActiveIsTrue();

    Optional<IntegrationSchedule> findByIntegration(Integration integration);

    List<IntegrationSchedule> findByIntegrationAndActiveIsTrue(Integration integration);
}
