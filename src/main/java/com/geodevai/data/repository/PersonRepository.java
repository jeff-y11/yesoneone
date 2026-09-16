package com.geodevai.data.repository;

import com.geodevai.data.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RepositoryRestResource(path = "person")
public interface PersonRepository extends JpaRepository<Person, UUID> {
}