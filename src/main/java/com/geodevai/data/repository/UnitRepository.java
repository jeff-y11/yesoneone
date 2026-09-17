package com.geodevai.data.repository;

import com.geodevai.data.model.Property;
import com.geodevai.data.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "unit")
public interface UnitRepository extends JpaRepository<Unit, UUID> {

    List<Unit> findByProperty(Property property);

    Optional<Unit> findByUnitNumberAndProperty(String unitNumber, Property property);

    Optional<Unit> findByExternalUnitId(String externalUnitId);
}
