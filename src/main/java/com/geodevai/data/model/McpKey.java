package com.geodevai.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mcp_key")
@Getter
@Setter
@NoArgsConstructor
public class McpKey extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mcpKeyId;

    private String name;

    private String keyHash;

    private String keyPrefix;

    private LocalDateTime expiresAt;

    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonProperty("createdAt")
    public LocalDateTime getCreatedAt() {
        return getAuditFields().getCreated();
    }
}
