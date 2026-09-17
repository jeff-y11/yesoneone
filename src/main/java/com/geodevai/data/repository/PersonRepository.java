package com.geodevai.data.repository;

import com.geodevai.data.model.Organization;
import com.geodevai.data.model.Person;
import com.geodevai.data.model.Unit;
import com.geodevai.data.model.User;
import com.geodevai.data.model.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Person p JOIN p.integration i WHERE i.organization = :organization")
    List<Person> findByOrganization(@Param("organization") Organization organization);

    @Query("SELECT p FROM Person p JOIN p.integration i WHERE i.organization = :organization AND (LOWER(p.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Person> searchByOrganizationAndQuery(@Param("organization") Organization organization, @Param("query") String query);
}
