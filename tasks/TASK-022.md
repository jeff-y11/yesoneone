# TASK-022: Create Database Migration Scripts

## Goal
Create database migration scripts for all new tables and columns added by TASK-017 through TASK-021. The project uses Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update` for development, but migration scripts should be provided for production deployments.

## Requirements

### Framework Assessment
The project uses Gradle (`build.gradle.kts`). Check if Flyway or Liquibase is configured. If neither is configured, document that `spring.jpa.hibernate.ddl-auto=update` handles schema evolution for development, and add Flyway as the migration framework for production consistency.

### Migration Script Requirements
Create migration scripts under `src/main/resources/db/migration/` (Flyway format) or equivalent for the chosen framework:

#### V1__Create_Address_Table.sql
```sql
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
```

#### V2__Create_Property_Table.sql
```sql
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
```

#### V3__Create_Unit_Table.sql
```sql
CREATE TABLE unit (
    unit_id UUID PRIMARY KEY,
    unit_number VARCHAR(50) NOT NULL,
    external_unit_id VARCHAR(255) NOT NULL,
    unit_type VARCHAR(255),
    square_footage INTEGER,
    bedrooms INTEGER,
    bathrooms INTEGER,
    rent_amount DECIMAL(19,2),
    is_occupied BOOLEAN DEFAULT FALSE,
    property_id UUID NOT NULL,
    address_id UUID,
    created_by UUID NOT NULL,
    created TIMESTAMP NOT NULL,
    modified_by UUID NOT NULL,
    modified TIMESTAMP NOT NULL,
    updated_source VARCHAR(255),
    created_by_name VARCHAR(255),
    modified_by_name VARCHAR(255),
    FOREIGN KEY (property_id) REFERENCES property(property_id),
    FOREIGN KEY (address_id) REFERENCES address(address_id)
);
```

#### V4__Update_Person_Table.sql
```sql
ALTER TABLE person
ADD COLUMN phone_number VARCHAR(255),
ADD COLUMN email VARCHAR(255),
ADD COLUMN external_tenant_id VARCHAR(255) NOT NULL,
ADD COLUMN lease_start_date TIMESTAMP,
ADD COLUMN lease_end_date TIMESTAMP,
ADD COLUMN unit_id UUID,
ADD FOREIGN KEY (unit_id) REFERENCES unit(unit_id);
```

#### V5__Create_Work_Order_Table.sql
```sql
CREATE TABLE work_order (
    work_order_id UUID PRIMARY KEY,
    external_work_order_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary TEXT,
    description TEXT,
    work_details TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50),
    work_order_type VARCHAR(50),
    amount DECIMAL(19,2),
    due_date TIMESTAMP,
    completion_date TIMESTAMP,
    entry_notes TEXT,
    vendor_notes TEXT,
    invoice_number VARCHAR(255),
    chargeable_to VARCHAR(255),
    call_source VARCHAR(255),
    caller_name VARCHAR(255),
    caller_contact_info VARCHAR(255),
    property_id UUID,
    unit_id UUID,
    tenant_id UUID,
    assigned_to VARCHAR(255),
    vendor_id VARCHAR(255),
    created_by UUID NOT NULL,
    created TIMESTAMP NOT NULL,
    modified_by UUID NOT NULL,
    modified TIMESTAMP NOT NULL,
    updated_source VARCHAR(255),
    created_by_name VARCHAR(255),
    modified_by_name VARCHAR(255),
    FOREIGN KEY (property_id) REFERENCES property(property_id),
    FOREIGN KEY (unit_id) REFERENCES unit(unit_id),
    FOREIGN KEY (tenant_id) REFERENCES person(person_id)
);
```

### Notes
- If Flyway/Liquibase is not yet configured, document this in the task and recommend adding it to `build.gradle.kts`
- All audit columns (`created_by`, `created`, `modified_by`, `modified`, `updated_source`, `created_by_name`, `modified_by_name`) follow the `AuditFields` embedded pattern
- All tables use `@Table(name = "...")` snake_case naming from the entity definitions
- If Flyway is not configured, the migration scripts should be noted as "to be added when Flyway/Liquibase is configured" and the `spring.jpa.hibernate.ddl-auto=update` approach should be confirmed for dev

## Acceptance Criteria
- All migration scripts exist and are syntactically valid SQL
- Foreign key constraints are correctly defined
- All audit columns are present in all tables
- `V4__Update_Person_Table.sql` correctly adds columns without breaking existing data
- If Flyway is configured, scripts execute successfully
- If Flyway is not configured, documentation explains the alternative approach

## Files Likely Involved
- `src/main/resources/db/migration/V1__Create_Address_Table.sql` (new)
- `src/main/resources/db/migration/V2__Create_Property_Table.sql` (new)
- `src/main/resources/db/migration/V3__Create_Unit_Table.sql` (new)
- `src/main/resources/db/migration/V4__Update_Person_Table.sql` (new)
- `src/main/resources/db/migration/V5__Create_Work_Order_Table.sql` (new)
- `build.gradle.kts` (modify — add Flyway/Liquibase dependency if needed)

## Dependencies
- TASK-017 through TASK-021 (all entity tasks)

## Constraints
- Migration scripts must be compatible with PostgreSQL (production) and H2 (development)
- All column names must match the `@Column` annotations in the entity classes
- Use `V{version}__{description}.sql` naming convention for Flyway
- If Flyway is not configured, still create the scripts and document the setup needed
