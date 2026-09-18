package com.geodevai.data.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "integration")
@Getter
@Setter
@NoArgsConstructor
public class Integration extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID integrationId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String endpoint;

    @Column(nullable = false)
    /** Valid values are defined by {@link com.geodevai.integration.IntegrationType} enum names. */
    private String type;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "integration_parameter", joinColumns = @JoinColumn(name = "integration_id"))
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> parameters;

    @OneToMany(mappedBy = "integration", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IntegrationCapabilities> capabilities = new HashSet<>();

    @Column(name = "api_key")
    private String apiKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}
