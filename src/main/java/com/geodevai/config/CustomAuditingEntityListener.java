package com.geodevai.config;

import com.geodevai.data.model.AuditFields;
import com.geodevai.data.model.AuditableEntity;
import com.geodevai.security.AuthChannelHolder;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Custom AuditingEntityListener that extends Spring's default listener to also populate
 * the {@code updatedSource}, {@code createdByName}, and {@code modifiedByName} fields
 * on {@link AuditFields} based on the current authentication context.
 *
 * The channel and display name are set via {@link AuthChannelHolder} (ThreadLocal) by
 * authentication filters (JWT for Web, API Key for MCP, etc.) before entity operations occur.
 */
public class CustomAuditingEntityListener extends AuditingEntityListener {

    private static final String SYSTEM_DISPLAY_NAME = "System";

    @PrePersist
    public void touchForCreate(Object source) {
        setAuditMetadata(source);
        super.touchForCreate(source);
    }

    @PreUpdate
    public void touchForUpdate(Object source) {
        setAuditMetadata(source);
        super.touchForUpdate(source);
    }

    private void setAuditMetadata(Object source) {
        if (!(source instanceof AuditableEntity entity)) {
            return;
        }

        AuditFields auditFields = entity.getAuditFields();

        String channel = AuthChannelHolder.getChannel();
        if (channel != null && !channel.isBlank()) {
            auditFields.setUpdatedSource(channel);
        }

        String displayName = AuthChannelHolder.getDisplayName();
        if (displayName == null || displayName.isBlank()) {
            displayName = SYSTEM_DISPLAY_NAME;
        }

        if (auditFields.getCreatedByName() == null) {
            auditFields.setCreatedByName(displayName);
        }
        auditFields.setModifiedByName(displayName);
    }
}
