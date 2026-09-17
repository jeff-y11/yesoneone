ALTER TABLE person
ADD COLUMN phone_number VARCHAR(255),
ADD COLUMN email VARCHAR(255),
ADD COLUMN external_tenant_id VARCHAR(255) NOT NULL,
ADD COLUMN lease_start_date TIMESTAMP,
ADD COLUMN lease_end_date TIMESTAMP,
ADD COLUMN unit_id UUID,
ADD FOREIGN KEY (unit_id) REFERENCES unit(unit_id);
