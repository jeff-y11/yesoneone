CREATE TABLE property (
    property_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    external_property_id VARCHAR(255) NOT NULL,
    property_type VARCHAR(255),
    number_of_units INTEGER,
    management_company VARCHAR(255),
    address_id UUID,
    organization_id UUID NOT NULL,
    created_by UUID NOT NULL,
    created TIMESTAMP NOT NULL,
    modified_by UUID NOT NULL,
    modified TIMESTAMP NOT NULL,
    updated_source VARCHAR(255),
    created_by_name VARCHAR(255),
    modified_by_name VARCHAR(255),
    FOREIGN KEY (address_id) REFERENCES address(address_id),
    FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);
