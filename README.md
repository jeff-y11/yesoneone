# GeodeVAI

A full-stack application with Spring Boot backend and React frontend.

## Backend
- Java 25
- Spring Boot 4.1+
- Gradle Kotlin DSL
- Lombok
- use spring profiles and application-<PROFILE>.properties as necessary
   - PostgreSQL in production
   - H2 in-memory database for development
- Spring Data JPA
- Spring Data REST

## Frontend
- React
- Vite
- Tailwind CSS
- Typescript

## Architecture
- Single deployable artifact
- React application served by Spring Boot
- Frontend and backend in the same repository
- Backend code under src/main/java
- Frontend code under src/main/react
- Data will be served on /data
- Services will be served on /services
- the MCP will be served on /mcp

## Development
Run frontend and backend as two separate processes so React hot reload works.

### 1. Frontend (port 5173)
```
cd src/main/react
npm install
npm run dev
```
- Vite proxies API requests to the backend on 8080

### 2. Backend (port 8080, DEV profile)
```
# PowerShell
$env:SPRING_PROFILES_ACTIVE="DEV"
gradle bootRun

# bash
SPRING_PROFILES_ACTIVE=DEV gradle bootRun
```
- `application-DEV.properties` selects H2 in-memory DB and allows CORS from localhost:5173
- `bootRun` runs with `src/main/resources` as its working directory, so `./application-secrets.properties` is found there (the secrets file is gitignored and never packaged into the jar)
- Create the secrets file once from the checked-in template: copy `src/main/resources/application-secrets.properties.example` to `application-secrets.properties` and fill in real values

## Production
Runs on the default profile (PostgreSQL settings in `application.properties`). No profile environment variable is needed.

```
java -jar geodevai-0.0.1-SNAPSHOT.jar
```
- The jar does not contain secrets. `application-secrets.properties` must be in the working directory of the process: Spring loads `./application-secrets.properties`, so the deployer chooses the directory to run from and places the secrets file there (start from `application-secrets.properties.example`)
- There will be an AWS loadbalancer feeding https requests to http://<lan-ip:8080
- Backend runs on port 8080
- Frontend is served from the same jar at port 8080 /index.html

## Guidelines
- Keep the structure simple.
- Prefer convention over configuration.
- Optimize for maintainability by a single developer.