package com.geodevai.data.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interface for entities that integrate with external APIs.
 * Provides fields to track the integration source, remote partner ID, and sync state.
 */
public interface Externalable {

    UUID getIntegrationId();

    void setIntegrationId(UUID integrationId);

    String getIntegrationRemoteId();

    void setIntegrationRemoteId(String remoteId);

    LocalDateTime getLastSyncTime();

    void setLastSyncTime(LocalDateTime lastSyncTime);
}
