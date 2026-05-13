# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

- **Run application**: `./mvnw spring-boot:run`
- **Run tests**: `./mvnw test`
- **Run single test**: `./mvnw test -Dtest=TestClassName`
- **Clean build**: `./mvnw clean install`
- **Skip tests**: `./mvnw install -DskipTests`

## Code Architecture

This is a Spring Boot REST API with JWT-based authentication using HTTP-only cookies.

### Core Components

1. **Authentication System** (`src/main/java/com/example/demo/controller/AuthController.java`):
   - Handles login, refresh, logout, registration, and user info endpoints
   - Uses JWT tokens stored in HttpOnly cookies for security
   - Implements token refresh mechanism

2. **Security Configuration** (`src/main/java/com/example/demo/configuration/SecurityConfig.java`):
   - Stateless session management
   - CORS configuration allowing localhost:5173 (frontend)
   - JWT authentication filter applied before UsernamePasswordAuthenticationFilter
   - Endpoint security: `/api/auth/**` public, all other endpoints require authentication

3. **JWT Handling**:
   - `JwtUtil`: Token generation, validation, and extraction
   - `JwtAuthFilter`: Extracts JWT from cookies and sets SecurityContext
   - Tokens: Access token (short-lived) and refresh token (longer-lived)

4. **Service Layer**:
   - `AuthService`: Handles authentication business logic (login, registration)
   - `EsempioService`: Business logic for example entity
   - Implementations in `service/impl` package

5. **Data Layer**:
   - Repositories: `UserRepository`, `EsempioRepository` (JPA)
   - Entities: `User`, `Esempio` (JPA/Hibernate)
   - DTOs: LoginRequest, RegisterRequest, EsempioDTO

6. **Controllers**:
   - `AuthController`: Authentication endpoints
   - `EsempioController`: CRUD operations for example entity
   - `AdminController`: Admin-protected endpoints

### Key Features

- **Cookie-based JWT**: Tokens stored in HttpOnly, Secure cookies with SameSite=Lax
- **Token Refresh**: Separate refresh endpoint to obtain new access tokens
- **Role-based Security**: Uses `@PreAuthorize` and `@PostAuthorize` annotations
- **CORS**: Configured for frontend integration with credentials support
- **Validation**: Uses Bean Validation (javax.validation) on request DTOs

### Project Structure

```
src/main/java/com/example/demo/
├── controller        # REST endpoints
├── dto              # Data Transfer Objects
├── configuration    # Spring config (Security, CORS)
├── entity           # JPA entities
├── repository       # Spring Data JPA repositories
├── service          # Service interfaces
├── service/impl     # Service implementations
├── components       # JWT utilities and filters
└── utility          # Helper classes
```

### Security Notes

- Passwords encoded with BCryptPasswordEncoder
- CSRF disabled (appropriate for stateless API)
- Session creation policy: STATELESS
- Authorization header not needed for CORS (auth via cookies)
- Set-Cookie header exposed for frontend cookie handling