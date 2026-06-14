# ProductService — REST Endpoints

Base URL: `http://localhost:8080`

All endpoints below (except `POST /login`) require a JWT in the request header:

```
Authorization: Bearer <access_token>
```

---

## Authentication Endpoint

### POST /login

Obtain a JWT by submitting username and password. No Bearer token required.

**Request**

```http
POST /login HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "username": "ravik775@gmail.com",
  "password": "Test1234"
}
```

**Success Response — 200 OK**

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

On successful login, the server logs authentication details at INFO level:

```
Authentication successful | user=ravik775@gmail.com | roles=[Admin] | authorities=[Admin, FACTOR_PASSWORD]
```

**Error Responses**

| Status | Cause |
|--------|-------|
| 401 | Invalid username or password |
| 500 | Server error |

---

## Product Endpoints

### GET /products/

List products with pagination.

**Authorization:** Any valid JWT (`isAuthenticated()`)

**Request**

```http
GET /products/?page=1&size=20 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Query Parameters**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | `1` | Page number |
| `size` | `20` | Page size |

**Success Response — 200 OK**

```json
[
  {
    "id": 1,
    "title": "Laptop",
    "price": 99999,
    "description": "Gaming laptop",
    "image": "laptop.png",
    "category": "Electronics"
  }
]
```

---

### GET /product/{id}

Get a single product by ID.

**Authorization:** Any valid JWT

**Request**

```http
GET /product/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Success Response — 200 OK**

```json
{
  "id": 1,
  "title": "Laptop",
  "price": 99999,
  "description": "Gaming laptop",
  "image": "laptop.png",
  "category": "Electronics"
}
```

**Error Responses**

| Status | Cause |
|--------|-------|
| 401 | Missing or invalid JWT |
| 404 | Product not found |

---

### POST /product

Create a new product.

**Authorization:** JWT with `Admin` authority (`@HasAuthority("Admin")`)

**Request**

```http
POST /product HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "title": "Wireless Mouse",
  "price": 1500,
  "description": "Ergonomic wireless mouse",
  "image": "mouse.png",
  "category": "Electronics"
}
```

**Success Response — 200 OK**

```json
{
  "id": 2,
  "title": "Wireless Mouse",
  "price": 1500,
  "description": "Ergonomic wireless mouse",
  "image": "mouse.png",
  "category": "Electronics"
}
```

**Error Responses**

| Status | Cause |
|--------|-------|
| 401 | Missing or invalid JWT |
| 403 | JWT valid but user lacks `Admin` authority |
| 400 | Validation failure (missing title, category, etc.) |

---

## Catalog / Category Endpoints

### GET /catalog/

List all categories (catalog view). Alias for `/category/`.

**Authorization:** Any valid JWT

**Request**

```http
GET /catalog/?page=1&size=20 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Query Parameters**

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | `1` | Page number |
| `size` | `20` | Page size |

**Success Response — 200 OK**

```json
[
  {
    "id": 1,
    "name": "Electronics",
    "description": "Electronic items"
  }
]
```

---

### GET /category/

Same as `GET /catalog/`. Returns paginated category list.

**Request**

```http
GET /category/?page=1&size=20 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### POST /category or POST /catalog

Create a new product category.

**Authorization:** JWT with `Admin` authority (`@HasAuthority("Admin")`)

**Request**

```http
POST /category HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "Books",
  "description": "Book catalog"
}
```

**Success Response — 200 OK**

```json
{
  "id": 2,
  "name": "Books",
  "description": "Book catalog"
}
```

---

### PUT /category/{id} or PUT /catalog/{id}

Update an existing category.

**Authorization:** JWT with `Admin` authority (`@HasAuthority("Admin")`)

**Request**

```http
PUT /catalog/1 HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Updated electronics catalog"
}
```

**Success Response — 200 OK**

```json
{
  "id": 1,
  "name": "Electronics",
  "description": "Updated electronics catalog"
}
```

---

## OAuth2 Token Endpoint (Token Issuance)

### POST /oauth2/token

Obtain a JWT via OAuth2 grant types. Uses HTTP Basic authentication with client credentials.

**Authorization Code exchange example** (after browser authorization):

```http
POST /oauth2/token HTTP/1.1
Host: localhost:8080
Authorization: Basic Y3JlZC1vaWRjLWNsaWVudDpzZWNyZXQ=
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&code=<auth_code>&redirect_uri=https://oauth.pstmn.io/v1/callback
```

**Client Credentials example** (machine-to-machine):

```http
POST /oauth2/token HTTP/1.1
Host: localhost:8080
Authorization: Basic dGVzdC1jbGllbnQ6c2VjcmV0
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&scope=api.read
```

**Success Response — 200 OK**

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "scope": "api.read"
}
```

Use `access_token` as the Bearer value for all REST API calls above.

---

## Endpoint Summary

| Method | Path | Auth Required | Authority |
|--------|------|---------------|-----------|
| `POST` | `/login` | None | — |
| `GET` | `/products/` | JWT Bearer | Authenticated |
| `GET` | `/product/{id}` | JWT Bearer | Authenticated |
| `POST` | `/product` | JWT Bearer | `Admin` |
| `GET` | `/catalog/` | JWT Bearer | `@PreAuthorize` — authenticated |
| `GET` | `/category/` | JWT Bearer | `@PreAuthorize` — authenticated |
| `POST` | `/category`, `/catalog` | JWT Bearer | `@HasAuthority('Admin')` |
| `PUT` | `/category/{id}`, `/catalog/{id}` | JWT Bearer | `@HasAuthority('Admin')` |
| `POST` | `/oauth2/token` | Client credentials | OAuth2 client |

---

## Common Error Responses

All errors return an `ErrorDTO`:

```json
{
  "message": "Access denied",
  "status": "FORBIDDEN",
  "path": "/product"
}
```

| HTTP Status | Meaning |
|-------------|---------|
| 401 Unauthorized | No JWT, expired JWT, or invalid signature |
| 403 Forbidden | Valid JWT but insufficient authority |
| 404 Not Found | Resource does not exist |
| 400 Bad Request | Validation or business rule failure |
| 500 Internal Server Error | Unexpected server error |
