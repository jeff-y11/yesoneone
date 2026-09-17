package com.geodevai.data.repository;

import com.geodevai.data.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RepositoryRestResource(path = "address")
public interface AddressRepository extends JpaRepository<Address, UUID> {
}
