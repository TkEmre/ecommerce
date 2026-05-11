# API Reference

Base URL: `http://localhost:8080/api/v1`

All protected endpoints require:
```
Authorization: Bearer <jwt_token>
```

---

## Auth

### POST `/auth/register`
Register a new user. No auth required.

**Request body:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Response:** `201 Created` — UserDto

---

### POST `/auth/login`
Login and receive a JWT token. No auth required.

**Request body:**
```json
{
  "username": "john",
  "password": "secret123"
}
```

**Response:** `200 OK` — JWT string

---

### POST `/auth/logout`
Blacklists the current token. Requires auth.

**Response:** `200 OK`

---

## Products

### GET `/products`
List all products (paginated). Public.

**Query params:** `page`, `size`, `sort` (e.g. `name,asc`)

**Response:** `200 OK` — `Page<ProductDto>`

---

### GET `/products/{id}`
Get product by ID. Public.

**Response:** `200 OK` — `ProductDto`

---

### GET `/products/category/{categoryName}`
List products by category (paginated). Public.

**Response:** `200 OK` — `Page<ProductDto>`

---

### POST `/products`
Create a product. **ADMIN only.**

**Request body:**
```json
{
  "name": "Laptop",
  "description": "...",
  "price": 999.99,
  "category": "electronics",
  "stockQuantity": 50
}
```

**Response:** `201 Created` — `ProductDto`

---

### PUT `/products/{id}`
Update a product. **ADMIN only.**

**Response:** `200 OK` — `ProductDto`

---

### DELETE `/products/{id}`
Delete a product. **ADMIN only.**

**Response:** `204 No Content`

---

### PATCH `/products/{id}/stock?quantity={n}`
Set stock quantity. **ADMIN only.**

**Response:** `200 OK` — `ProductDto`

---

## Orders

### POST `/orders`
Create an order. Requires auth.

**Request body:**
```json
{
  "addressId": 1,
  "orderItems": [
    { "productId": 3, "quantity": 2 }
  ]
}
```

**Response:** `201 Created` — `OrderDto`

---

### GET `/orders`
List current user's orders (paginated). Requires auth.

**Query params:** `page`, `size`, `sort` (default: `orderDate,desc`)

**Response:** `200 OK` — `Page<OrderDto>`

---

### GET `/orders/{id}`
Get order by ID (owner or ADMIN). Requires auth.

**Response:** `200 OK` — `OrderDto`

---

### PUT `/orders/{id}/status`
Update order status. **ADMIN only.**

**Request body:**
```json
{ "status": "SHIPPED" }
```

Possible values: `PENDING` `CONFIRMED` `SHIPPED` `DELIVERED` `CANCELLED`

**Response:** `200 OK` — `OrderDto`

---

### DELETE `/orders/{id}`
Cancel an order (owner or ADMIN). Requires auth.

**Response:** `204 No Content`

---

## Users

### GET `/users/profile`
Get current user's profile. Requires auth.

**Response:** `200 OK` — `UserDto`

---

### PUT `/users/profile`
Update current user's profile. Requires auth.

**Request body:**
```json
{
  "username": "newname",
  "email": "new@example.com"
}
```

**Response:** `200 OK` — `UserDto`

---

### POST `/users/address`
Add a shipping address. Requires auth.

**Request body:**
```json
{
  "street": "123 Main St",
  "city": "Istanbul",
  "country": "Turkey",
  "zipCode": "34000"
}
```

**Response:** `200 OK` — `AddressDto`

---

### GET `/users/all`
List all users (paginated). **ADMIN only.**

**Response:** `200 OK` — `Page<UserDto>`
