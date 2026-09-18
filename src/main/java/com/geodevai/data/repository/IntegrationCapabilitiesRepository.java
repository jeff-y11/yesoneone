package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.IntegrationCapabilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IntegrationCapabilitiesRepository extends JpaRepository<IntegrationCapabilities, UUID> {

    List<IntegrationCapabilities> findByIntegration(Integration integration);
}
