package com.geodevai.data.model;

import com.geodevai.security.OAuth2Provider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Login extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID loginId;

    private Boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;
    private String providerId;

    @Column(length = 2048)
    private String accessToken;
    @Column(length = 2048)
    private String refreshToken;
    private Instant tokenExpiry;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private User user;
}