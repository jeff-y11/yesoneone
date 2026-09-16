# TASK-001: Package Version Upgrades (Backend & Frontend)

## Goal
Upgrade the third-party libraries, frameworks, and tools in both the backend (Spring Boot, Gradle plugins, JJWT) and frontend (React, Vite, Tailwind, Typescript) to the latest stable and secure versions.

## Existing Architecture
- **Backend:**
  - Build system: Gradle Kotlin DSL (`build.gradle.kts`)
  - Spring Boot plugin version: `4.1.0`
  - Spring Dependency Management plugin version: `1.1.6`
  - Kotlin plugins (JVM, Spring, JPA, Lombok) version: `2.4.10`
  - JJWT (JSON Web Token) version: `0.12.6`
- **Frontend:**
  - Build system: Vite + NPM (`package.json`)
  - React / React DOM: `^19.1.0`
  - React Router DOM: `^6.30.6`
  - Tailwind CSS / @tailwindcss/vite: `^4.1.0`
  - TypeScript: `^5.8.0`
  - Vite: `^6.3.0`
  - @vitejs/plugin-react: `^4.4.0`

## Requirements
1. **Backend Upgrades:**
   - Upgrade Spring Boot plugin from `4.1.0` to `4.1.1`.
   - Upgrade `io.spring.dependency-management` plugin from `1.1.6` to `1.1.7`.
   - Ensure Kotlin plugins are aligned and updated if compatible (e.g., `2.4.10` to newest stable).
   - Upgrade JJWT dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) from `0.12.6` to `0.12.7`.
2. **Frontend Upgrades:**
   - Upgrade `react` and `react-dom` to `^19.3.0` (or latest stable React 19 minor version).
   - Upgrade `@types/react` and `@types/react-dom` to match React's new version (`^19.3.0`).
   - Upgrade `react-router-dom` to `^6.30.7` or latest stable 6.x / 7.x compatible with React 19.
   - Upgrade `tailwindcss` and `@tailwindcss/vite` to their latest stable 4.x releases.
   - Upgrade `typescript` to `^5.8.2` or latest stable 5.x.
   - Upgrade `vite` and `@vitejs/plugin-react` to latest stable releases.
3. **Verification:**
   - Verify that both backend and frontend build successfully after the upgrades.
   - Run existing unit/integration tests to ensure no breaking changes were introduced.

## Acceptance Criteria
- `build.gradle.kts` updated with the specified plugin and dependency versions.
- `package.json` in `src/main/react` updated with the specified packages.
- Frontend builds cleanly without TS/compilation errors when running `npm run build` or `npm run typecheck`.
- Backend builds and passes tests when running `gradle build jar`.
- Local dev environment runs cleanly (frontend hot reloading on 5173, backend running on 8080).

## Files Likely Involved
- `D:\workspace\geodevai\build.gradle.kts`
- `D:\workspace\geodevai\src\main\react\package.json`

## Constraints & Dependencies
- Compatibility: Ensure Spring Boot `4.1.1` is fully compatible with Java 25.
- Compatibility: Ensure React `19.3.0` does not introduce type-checking mismatches with existing React Router or Tailwind dependencies.
