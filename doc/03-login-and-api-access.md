# ProductService — Login Methods and API Access

This document describes every way to obtain a JWT and use it to access protected REST endpoints.

---

## Core Rule

> **All REST APIs (`/products`, `/catalog`, `/category`, `/product`) require a JWT Bearer token in the header. Session cookies from form login do NOT grant API access.**

```
Authorization: Bearer <access_token>
```

Both login paths below produce JWTs that are validated by the same Resource Server filter chain.

---

## Method 1: REST Login API (Recommended for SPAs and API Clients)

Best for: mobile apps, SPAs, Postman, automated scripts.

### Step 1 — Obtain JWT

```http
POST http://localhost:8080/login
Content-Type: application/json

{
  "username": "ravik775@gmail.com",
  "password": "Test1234"
}
```

**Response:**

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJzdWIiOiJyYXZpazc3NUBnbWFpbC5jb20iLCJhdXRob3JpdGllcyI6WyJBZG1pbiJdLCJpYXQiOjE3MTgzODQwMDAsImV4cCI6MTcxODM4NzYwMH0...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

**Server log (INFO)** after successful authentication:

```
Authentication successful | user=ravik775@gmail.com | roles=[Admin] | authorities=[Admin, FACTOR_PASSWORD]
```

| Log field | Meaning |
|-----------|---------|
| `user` | Username that authenticated |
| `roles` | Roles from database (`User.roles`) |
| `authorities` | Spring Security granted authorities (includes roles plus framework entries) |

### Step 2 — Call Protected API

Copy `accessToken` and attach it to every subsequent request:

```http
GET http://localhost:8080/products/
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

### What Happens Internally

1. `AuthController` receives credentials
2. `AuthenticationManager` validates against MySQL via `CustomUserDetailService`
3. `JwtEncoder` signs a token with RSA private key
4. Token includes `authorities` claim from user's roles in database
5. Token expires in **3600 seconds (1 hour)**

---

## Method 2: OAuth2 Client Credentials Grant

Best for: service-to-service (machine-to-machine) communication.

### Prerequisites

- OAuth2 client registered in database (run `OAuthClientInsertTest` or insert manually)
- Client must support `client_credentials` grant type

### Step 1 — Request Token

```http
POST http://localhost:8080/oauth2/token
Authorization: Basic <base64(client_id:client_secret)>
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&scope=api.read
```

Example with `test-client` / `secret`:

```
Authorization: Basic dGVzdC1jbGllbnQ6c2VjcmV0
```

**Response:**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "api.read"
}
```

### Step 2 — Call Protected API

```http
GET http://localhost:8080/catalog/
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

> **Note:** Client credentials tokens authenticate the **client**, not a user. They can read products/catalog but cannot create products (requires `Admin` user authority).

---

## Method 3: OAuth2 Authorization Code Flow (Browser + Postman)

Best for: third-party apps, Postman OAuth2 helper, web applications with redirect.

### Prerequisites

- OAuth2 client `cred-oidc-client` registered (run `OAuthClientInsertTest`)
- Redirect URI: `https://oauth.pstmn.io/v1/callback` (for Postman)

### Step 1 — Authorize (Browser)

Open in browser:

```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=cred-oidc-client&scope=openid%20profile&redirect_uri=https://oauth.pstmn.io/v1/callback
```

You will be redirected to the login page (`GET /login`).

### Step 2 — Form Login (Browser)

Submit credentials via the HTML form (posts to `/perform_login`):

| Field | Value |
|-------|-------|
| Username | `ravik775@gmail.com` |
| Password | `Test1234` |

After successful login, the browser is redirected to the callback URL with an authorization `code`.

### Step 3 — Exchange Code for Token

```http
POST http://localhost:8080/oauth2/token
Authorization: Basic Y3JlZC1vaWRjLWNsaWVudDpzZWNyZXQ=
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=<authorization_code>&redirect_uri=https://oauth.pstmn.io/v1/callback
```

Where `Y3JlZC1vaWRjLWNsaWVudDpzZWNyZXQ=` is Base64 of `cred-oidc-client:secret`.

**Response:**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "openid profile"
}
```

The OAuth2 token customizer embeds user `authorities` (e.g. `Admin`) in the JWT when a user authenticates.

### Step 4 — Call Protected API

```http
POST http://localhost:8080/product
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "New Product",
  "price": 500,
  "description": "Created via OAuth2 token",
  "categoryName": "Electronics"
}
```

---

## Comparison of Login Methods

| Method | Use Case | User Context | Admin Create |
|--------|----------|--------------|--------------|
| `POST /login` | SPA, mobile, scripts | Yes (from DB roles) | Yes, if user has `Admin` |
| OAuth2 Client Credentials | Service-to-service | No (client only) | No |
| OAuth2 Authorization Code | Web apps, Postman | Yes (from DB roles) | Yes, if user has `Admin` |

---

## Complete Walkthrough Example (curl)

### 1. Login

```bash
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"ravik775@gmail.com","password":"Test1234"}'
```

Save the `accessToken` from the response.

### 2. List Products

```bash
curl -s http://localhost:8080/products/ \
  -H "Authorization: Bearer <accessToken>"
```

### 3. List Catalog

```bash
curl -s http://localhost:8080/catalog/ \
  -H "Authorization: Bearer <accessToken>"
```

### 4. Create Product (Admin only)

```bash
curl -s -X POST http://localhost:8080/product \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "USB Cable",
    "price": 299,
    "description": "Type-C cable",
    "category": "Electronics"
  }'
```

### 6. Create Category (Admin only)

```bash
curl -s -X POST http://localhost:8080/category \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Books",
    "description": "Book catalog"
  }'
```

### 7. Update Category (Admin only)

```bash
curl -s -X PUT http://localhost:8080/catalog/1 \
  -H "Authorization: Bearer <accessToken>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Updated electronics catalog"
  }'
```

---

## Token Expiry and Renewal

| Source | Expiry | Renewal |
|--------|--------|---------|
| `POST /login` | 3600 seconds | Re-login |
| OAuth2 access token | Configured by auth server (default ~1 hour) | Use `refresh_token` (authorization code flow) or re-authenticate |
| OAuth2 client credentials | Configured by auth server | Re-request token |

When a token expires, APIs return **401 Unauthorized**. Obtain a new token using any method above.

---

## Seeding Test Users and OAuth Clients

### Create first user

Run the JUnit test once:

```
org.bgm.productservice.once.CreateFirstUserTest#addUser
```

Default user:

| Field | Value |
|-------|-------|
| Username | `ravik775@gmail.com` |
| Password | `Test1234` |
| Role | `Admin` |

### Create OAuth2 client

Run the JUnit test once:

```
org.bgm.productservice.once.OAuthClientInsertTest#addSampleClientToDB
```

Default client:

| Field | Value |
|-------|-------|
| Client ID | `cred-oidc-client` |
| Client Secret | `secret` |
| Grant Types | `authorization_code`, `refresh_token` |
| Redirect URI | `https://oauth.pstmn.io/v1/callback` |
