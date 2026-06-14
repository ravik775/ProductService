# ProductService Documentation

Product Service application built with Spring Boot, secured with JWT and OAuth2.

## Documentation Index

| Document | Description |
|----------|-------------|
| [01-application-architecture.md](01-application-architecture.md) | Architecture, tech stack, security layers, data model |
| [02-rest-endpoints.md](02-rest-endpoints.md) | REST API reference with JWT examples |
| [03-login-and-api-access.md](03-login-and-api-access.md) | Login methods and how to access protected APIs |
| [04-postman-configuration.md](04-postman-configuration.md) | Postman setup for REST login, OAuth2, and API calls |
| [05-spring-security-flow.md](05-spring-security-flow.md) | Filter chains, JWT flow, `@PreAuthorize` / `@HasAuthority` |

## Quick Links

- **Login (JWT):** `POST /login` with JSON credentials
- **Read APIs:** require `Authorization: Bearer <token>` and `@PreAuthorize("isAuthenticated()")`
- **Write APIs:** require Bearer token with `Admin` role (`@HasAuthority("Admin")`)
- **Security deep dive:** see [Why `@PreAuthorize("isAuthenticated()")` is needed](05-spring-security-flow.md#why-preauthorizeisauthenticated-is-needed) in doc 05
