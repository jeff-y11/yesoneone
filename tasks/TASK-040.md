# TASK-040: Integration credentials

## Goal

Update the integration scaffolding to support an API key credential for integrations that require one. The Buildium integration should use the API key to authenticate its API calls, following the existing project's conventions for credential storage and security.

## Requirements

### API Key Field on Integration

Add an API key credential field to the `Integration` entity:

- **Field name**: `apiKey` (or similar)
- **Type**: String
- **Notes**: Store the API key for integrations that require authentication (e.g., Buildium). Follow existing security conventions for sensitive data.

### Existing Conventions to Follow

Review existing credential/storage patterns in the codebase:

- `McpKey` entity - study how keys are stored and secured
- Any existing encryption/encoding patterns used in the project
- The `parameters` field on `Integration` - may be used for configuration

### BuildiumIntegrationRunner Credential Use

Update `BuildiumIntegrationRunner` to:

- Retrieve the API key from the integration's credentials
- Use the API key to authenticate API calls via the `Authorization` header (as specified in the Buildium OpenAPI spec)
- Follow the existing authentication pattern used by other integrations

### Security Considerations

- Ensure API key is not exposed in logs or error messages
- Follow the project's existing secret management conventions
- The API key should be accessible to the runner but secured appropriately

## Files Likely Involved

- `src/main/java/com/geodevai/data/model/Integration.java` - Add `apiKey` field
- `src/main/java/com/geodevai/integration/BuildiumIntegrationRunner.java` - Use API key for auth
- `src/main/java/com/geodevai/data/repository/IntegrationRepository.java` - If needed for credential queries
- Any existing security/configuration files that handle credentials

## Acceptance Criteria

- `Integration` entity has an `apiKey` field for storing API credentials
- `BuildiumIntegrationRunner` uses the API key to authenticate API calls via `Authorization` header
- Credential storage follows existing project conventions
- No hard-coded credentials or insecure handling