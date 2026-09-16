package com.geodevai.data.repository;

import com.geodevai.data.model.McpKey;
import com.geodevai.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "mcpkey")
public interface McpKeyRepository extends JpaRepository<McpKey, UUID> {

    Optional<McpKey> findByKeyHash(String keyHash);

    Optional<McpKey> findByUserAndActiveIsTrue(User user);

    List<McpKey> findAllByUserAndActiveIsTrue(User user);
}