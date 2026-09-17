package com.geodevai.data.repository;

import com.geodevai.data.model.Person;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.User;
import com.geodevai.data.model.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "person")
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByPhoneNumber(String phoneNumber);

    Optional<Person> findByEmail(String email);

    Optional<Person> findByExternalTenantId(String externalTenantId);

    List<Person> findByUnit(Unit unit);

    List<Person> findByIntegration(Integration integration);

    List<Person> findByUser(User user);
}
