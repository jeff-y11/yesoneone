package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RepositoryRestResource(path = "organization")
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
}
