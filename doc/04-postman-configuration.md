# ProductService — Postman Configuration

This guide walks through configuring Postman to authenticate and call ProductService REST APIs.

**Base URL:** `http://localhost:8080`

---

## Prerequisites

1. ProductService running locally on port 8080
2. MySQL database available with seeded user (run `CreateFirstUserTest`)
3. OAuth2 client seeded (run `OAuthClientInsertTest`) — needed for OAuth2 flows only

---

## Option A: REST Login (Simplest)

Use this when you want to quickly test APIs with a user JWT.

### Step 1 — Create a Login Request

| Setting | Value |
|---------|-------|
| Method | `POST` |
| URL | `http://localhost:8080/login` |

**Headers**

| Key | Value |
|-----|-------|
| `Content-Type` | `application/json` |

**Body** (raw JSON)

```json
{
  "username": "ravik775@gmail.com",
  "password": "Test1234"
}
```

Click **Send**. Copy `accessToken` from the response.

### Step 2 — Configure Bearer Token on API Requests

For each protected request (`GET /products/`, `GET /catalog/`, etc.):

1. Open the request in Postman
2. Go to the **Authorization** tab
3. Type: **Bearer Token**
4. Token: paste the `accessToken` value

Or set manually in **Headers**:

| Key | Value |
|-----|-------|
| `Authorization` | `Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...` |

### Step 3 — Automate Token Capture (Optional)

Add this to the **Tests** tab of the Login request:

```javascript
if (pm.response.code === 200) {
    var json = pm.response.json();
    pm.collectionVariables.set("access_token", json.accessToken);
}
```

Then on other requests, set Authorization Bearer Token to:

```
{{access_token}}
```

---

## Option B: OAuth2 Authorization Code (Postman Built-in)

Use this to test the full OAuth2 browser flow with Postman's OAuth helper.

### Step 1 — Create a Collection Variable

In your Postman collection, add variables:

| Variable | Value |
|----------|-------|
| `base_url` | `http://localhost:8080` |
| `client_id` | `cred-oidc-client` |
| `client_secret` | `secret` |
| `redirect_uri` | `https://oauth.pstmn.io/v1/callback` |
| `access_token` | *(leave empty — set by OAuth)* |

### Step 2 — Configure Collection Authorization

1. Select your collection → **Authorization** tab
2. Type: **OAuth 2.0**
3. Configure:

| Field | Value |
|-------|-------|
| Token Name | `ProductService Token` |
| Grant Type | `Authorization Code` |
| Callback URL | `https://oauth.pstmn.io/v1/callback` |
| Auth URL | `{{base_url}}/oauth2/authorize` |
| Access Token URL | `{{base_url}}/oauth2/token` |
| Client ID | `{{client_id}}` |
| Client Secret | `{{client_secret}}` |
| Scope | `openid profile` |
| State | *(auto-generated)* |
| Client Authentication | `Send as Basic Auth header` |

4. Click **Get New Access Token**
5. A browser window opens → log in with `ravik775@gmail.com` / `Test1234`
6. Postman captures the token — click **Use Token**

All requests in the collection inherit this Bearer token.

### Step 3 — Call APIs

Create requests under the collection (they inherit OAuth2 token):

```
GET {{base_url}}/products/
GET {{base_url}}/catalog/
POST {{base_url}}/product
```

---

## Option C: OAuth2 Client Credentials

Use this for machine-to-service testing without a user login.

### Request Configuration

| Setting | Value |
|---------|-------|
| Method | `POST` |
| URL | `http://localhost:8080/oauth2/token` |

**Authorization tab**

| Field | Value |
|-------|-------|
| Type | Basic Auth |
| Username | `test-client` |
| Password | `secret` |

> Ensure a client with `client_credentials` grant exists in the database. The integration test seeds `test-client` / `secret`.

**Body** (x-www-form-urlencoded)

| Key | Value |
|-----|-------|
| `grant_type` | `client_credentials` |
| `scope` | `api.read` |

**Tests tab** (optional — save token):

```javascript
if (pm.response.code === 200) {
    var json = pm.response.json();
    pm.collectionVariables.set("access_token", json.access_token);
}
```

Use `{{access_token}}` as Bearer token on subsequent requests.

---

## Sample Postman Collection Requests

### 1. Login

```
POST {{base_url}}/login
Content-Type: application/json

{"username":"ravik775@gmail.com","password":"Test1234"}
```

### 2. Get Products

```
GET {{base_url}}/products/?page=1&size=20
Authorization: Bearer {{access_token}}
```

### 3. Get Product by ID

```
GET {{base_url}}/product/1
Authorization: Bearer {{access_token}}
```

### 4. Get Catalog

```
GET {{base_url}}/catalog/?page=1&size=20
Authorization: Bearer {{access_token}}
```

### 5. Create Product (Admin)

```
POST {{base_url}}/product
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
  "title": "Bluetooth Speaker",
  "price": 2999,
  "description": "Portable speaker",
  "image": "speaker.png",
  "category": "Electronics"
}
```

### 6. Create Category (Admin)

```
POST {{base_url}}/category
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
  "name": "Books",
  "description": "Book catalog"
}
```

### 7. Update Category (Admin)

```
PUT {{base_url}}/catalog/1
Authorization: Bearer {{access_token}}
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Updated electronics catalog"
}
```

---

## Postman Environment Setup

Create a Postman **Environment** named `ProductService Local`:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `username` | `ravik775@gmail.com` | `ravik775@gmail.com` |
| `password` | `Test1234` | `Test1234` |
| `client_id` | `cred-oidc-client` | `cred-oidc-client` |
| `client_secret` | `secret` | `secret` |
| `access_token` | | *(populated after login)* |

Reference variables as `{{base_url}}`, `{{access_token}}`, etc.

---

## Troubleshooting

| Problem | Cause | Fix |
|---------|-------|-----|
| 401 on API call | Missing or expired token | Re-run login or refresh OAuth token |
| 401 on `/login` | Wrong credentials | Verify user exists in DB |
| 403 on `POST /product` or category APIs | User lacks `Admin` role | Login as Admin user |
| OAuth redirect fails | Client not in DB | Run `OAuthClientInsertTest` |
| `invalid_client` on token | Wrong client secret | Secret must match DB (BCrypt-encoded) |
| Token works once then fails | App restarted (new RSA key) | Get a new token after restart |
| CORS errors | Calling from browser origin | Use Postman desktop or configure CORS |

---

## Postman OAuth2 vs REST Login — When to Use Which

| Scenario | Recommended |
|----------|-------------|
| Quick API testing | Option A — `POST /login` |
| Testing OAuth2 integration | Option B — Authorization Code |
| Testing service accounts | Option C — Client Credentials |
| Automated collection runs | Option A with collection variable script |
