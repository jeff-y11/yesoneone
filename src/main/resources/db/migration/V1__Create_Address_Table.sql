CREATE TABLE address (
    address_id UUID PRIMARY KEY,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    address_line3 VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    postal_code VARCHAR(20),
    country VARCHAR(255),
    created_by UUID NOT NULL,
    created TIMESTAMP NOT NULL,
    modified_by UUID NOT NULL,
    modified TIMESTAMP NOT NULL,
    updated_source VARCHAR(255),
    created_by_name VARCHAR(255),
    modified_by_name VARCHAR(255)
);
