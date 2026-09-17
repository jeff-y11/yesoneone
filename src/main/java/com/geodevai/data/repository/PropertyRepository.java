package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "property")
public interface PropertyRepository extends JpaRepository<Property, UUID> {

    Optional<Property> findByExternalPropertyId(String externalPropertyId);

    List<Property> findByOrganization(Organization organization);
}
