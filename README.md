# Participant Bank Backend Service

Spring Boot backend service for managing Participant Banks and Authentication.

## Features
- **Authentication**: Authenticates user against static credentials, logs into Keycloak (`http://192.168.20.49:9091/realms/acs`), and automatically synchronizes/registers the user record in PostgreSQL (`tbl_users`).
- **Participant Bank CRUD**: Clean JSON endpoints to create, update, retrieve, list, search, and delete participant banks (`tbl_participant_banks`).
- **PostgreSQL Database**: Configured for PostgreSQL with `dev` and `prod` profiles.
- **Logging**: Configured via Logback (`logback-spring.xml`) with rolling file appenders and console logs.
- **Swagger / OpenAPI Documentation**: Available at `http://localhost:9081/swagger-ui.html` and `http://localhost:9081/v3/api-docs` with comprehensive request samples and **only 200 (OK) response samples**.

---

## Git Branches Structure

| Branch Name | Access Token Required | Logo Capability | Description |
| :--- | :--- | :--- | :--- |
| **`branch1-no-auth-no-logo`** | ❌ No | ❌ Optional / Not Required | Open endpoints for rapid frontend testing. Pure JSON CRUD for participant banks. |
| **`branch2-no-auth-with-logo`** | ❌ No | ✅ Full Upload & Storage | Open endpoints with complete logo upload (`POST /api/v1/participants/{id}/logo`), storage, and static serving via `/uploads/logos/**`. |
| **`branch3-auth-with-logo`** | ✅ Yes (Keycloak JWT) | ✅ Full Upload & Storage | Enforces Bearer JWT access token authentication for all protected endpoints except `/api/v1/auth/login` and public assets. |

---

## Configuration Profiles
- **`dev`** (Default):
  - Database: `jdbc:postgresql://localhost:5432/acs_participant_db`
  - Username: `postgres` / Password: `postgres`
  - Hibernate DDL: `update`
- **`prod`**:
  - Database: Configurable via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
  - Production logging and security settings.

---

## Authentication Details
- **Login Endpoint**: `POST /api/v1/auth/login` (also mapped to `/acs-dev/api/v1/login`)
- **Default Static Credentials**:
  - Username: `admin`
  - Password: `Password@123`
- **Keycloak Realm**: `http://192.168.20.49:9091/realms/acs` (Client ID: `acs_apis_client_api`)

---

## Running the Application
```bash
# Build
./gradlew build

# Run in Development mode
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run in Production mode
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Access Swagger UI: `http://localhost:9081/swagger-ui.html`
