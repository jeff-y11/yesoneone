package com.geodevai.data.repository;

import com.geodevai.data.model.Login;
import com.geodevai.security.OAuth2Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource(path = "login")
public interface LoginRepository extends JpaRepository<Login, UUID> {

    Optional<Login> findByProviderAndProviderId(OAuth2Provider provider, String providerId);
}