# Architecture

## Overview

```
Browser
  │
  ▼
React Frontend (localhost:5173)
  │  Axios + JWT header
  │  Vite proxy → /api/* → localhost:8080
  │  Vite proxy → /uploads/* → localhost:8080
  ▼
Spring Boot Backend (localhost:8080)
  │
  ├── Spring Security (JWT filter)
  ├── REST Controllers
  ├── Service layer
  └── JPA Repositories
        │
        ▼
      H2 Database (file: ./data/ecommerce_db)
        +
      ./uploads/products/   (product images on disk)
```

---

## Backend

### Layers

| Layer | Package | Responsibility |
|-------|---------|----------------|
| Controller | `controller/` | HTTP routing, request/response mapping |
| Service | `service/` | Business logic |
| Repository | `repository/` | JPA data access |
| DTO | `dto/` | API data shapes (no entity leaks) |
| Model | `model/` | JPA entities |
| Security | `security/` | JWT filter, token util, blacklist |
| Config | `config/` | Security chain, CORS, OpenAPI, static resources |

### Auth Flow

1. Client sends `POST /api/v1/auth/login`
2. Server validates credentials, returns signed JWT string
3. Client stores token in `localStorage`
4. Every subsequent request includes `Authorization: Bearer <token>`
5. `JwtRequestFilter` validates the token before each request
6. `POST /api/v1/auth/logout` adds token to in-memory blacklist

### Roles

| Role | Access |
|------|--------|
| `ROLE_USER` | Browse products, manage own orders, own profile |
| `ROLE_SELLER` | All USER permissions + product CRUD + image upload |
| `ROLE_ADMIN` | All SELLER permissions + manage all orders and users |

### Image Upload

- Endpoint: `POST /api/v1/products/{id}/image` (multipart/form-data, field name `file`)
- Files saved to `./uploads/products/{uuid}.{ext}` relative to the backend working directory
- `imageUrl` stored as `/uploads/products/{filename}` in the product record
- Static files served via `WebMvcConfig` resource handler at `/uploads/**`
- Vite dev server proxies `/uploads/*` to `localhost:8080`

### Public Endpoints (no auth required)

```
GET  /api/v1/products
GET  /api/v1/products/{id}
GET  /api/v1/products/category/{name}
POST /api/v1/auth/register
POST /api/v1/auth/login
GET  /uploads/**
```

---

## Frontend

### Structure

```
src/
├── api/
│   └── client.js           Axios instance with JWT interceptor + 401 handler
├── context/
│   └── AuthContext.jsx     Token state, user info (isAdmin, isSeller), login/logout
├── components/
│   ├── Navbar.jsx
│   ├── ProtectedRoute.jsx  Route guards: ProtectedRoute, AdminRoute, SellerRoute
│   └── ui/                 shadcn-style components (Button, Input, Badge, Dialog, …)
└── pages/
    ├── Login.jsx
    ├── Register.jsx         Buyer / Seller account type toggle
    ├── Products.jsx         Paginated grid, category filter, search, product images
    ├── ProductDetail.jsx    Quantity picker, add to cart, product image
    ├── Cart.jsx             localStorage cart, guest/member checkout buttons
    ├── Checkout.jsx         Multi-step: Auth (email only for guests) → Address → Payment
    ├── Orders.jsx           Order history with status badges
    ├── OrderDetail.jsx      Item breakdown, cancel action
    ├── Profile.jsx          Edit profile, manage addresses
    ├── admin/
    │   ├── AdminLayout.jsx      Sidebar layout
    │   ├── AdminProducts.jsx    CRUD table, stock modal, image upload
    │   ├── AdminOrders.jsx      Status update modal
    │   └── AdminUsers.jsx       User list (read-only)
    └── seller/
        ├── SellerLayout.jsx     Sidebar layout
        └── SellerProducts.jsx   Product management with image upload
```

### State Management

| State | Storage | Notes |
|-------|---------|-------|
| JWT token | `localStorage` | Persists across page reloads |
| User info | React Context | Derived from JWT payload on load |
| Cart | `localStorage` | Persists across page reloads |
| Page data | Component state | Fetched on mount / page change |

### Route Guards

| Guard | Condition | Redirects to |
|-------|-----------|-------------|
| `ProtectedRoute` | Requires valid token | `/login` |
| `AdminRoute` | Requires token + `isAdmin` | `/` |
| `SellerRoute` | Requires token + `isSeller` or `isAdmin` | `/` |

> `/checkout` has no route guard — auth is handled inside the component so guests can access it.

### Guest Checkout Flow

1. User clicks "Continue as guest" in the cart
2. Checkout prompts for email only
3. A random password is auto-generated; account is created and logged in silently
4. User proceeds to Address → Payment as a normal logged-in user
