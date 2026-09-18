CREATE TABLE integration_capability (
    capability_id UUID PRIMARY KEY,
    integration_id UUID NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    direction VARCHAR(20) NOT NULL,
    required BOOLEAN NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP NOT NULL,
    FOREIGN KEY (integration_id) REFERENCES integration(integration_id)
);
