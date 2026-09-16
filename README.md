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
- Frontend runs on port 5173 from src/main/react
  - the build process will actually copy the artifact to be served from 8080, but running it separately allows for hot deployment
- Backend runs on port 8080
- Vite proxy forwards API requests to backend

## Production
- There will be an AWS loadbalancer feeding https requests to http://<lan-ip:8080
- Backend stays the same at port 8080
- Frontend will also be served at port 8080 /index.html

## Guidelines
- Keep the structure simple.
- Prefer convention over configuration.
- Optimize for maintainability by a single developer.