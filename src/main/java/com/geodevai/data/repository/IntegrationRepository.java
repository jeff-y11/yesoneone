package com.geodevai.data.repository;

import com.geodevai.data.model.Integration;
import com.geodevai.data.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntegrationRepository extends JpaRepository<Integration, UUID> {

    Optional<Integration> findByNameAndOrganizationAndActiveIsTrue(String name, Organization organization);

    List<Integration> findByOrganizationAndActiveIsTrue(Organization organization);

    Optional<Integration> findByIntegrationIdAndActiveIsTrue(UUID integrationId);
}
