# storé — E-Commerce Platform

A full-stack e-commerce application with a Spring Boot REST API backend and a React frontend. Supports three user roles: buyers, sellers, and admins.

## Project Structure

```
e-commerce/
├── backend/       Spring Boot REST API (Java 17, H2, JWT)
├── frontend/      React + Vite + Tailwind CSS
└── docs/          API reference and architecture notes
```

## Architecture

```
Browser
  │
  ▼
React Frontend (localhost:5173)
  │  Axios + JWT header
  │  Vite proxy → /api/* and /uploads/* → localhost:8080
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
```

## Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- Maven (or use the included `mvnw` wrapper)

### 1. Backend

```bash
cd backend

# Windows (PowerShell)
.\mvnw spring-boot:run

# Windows (Command Prompt)
mvnw spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

API → `http://localhost:8080`  
Swagger UI → `http://localhost:8080/swagger-ui.html`

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

App → `http://localhost:5173`

> Both servers must be running at the same time.

## Default Accounts

| Email | Password | Role |
|-------|----------|------|
| `admin@store.com` | `admin123` | Admin |

The admin account and 15 sample products are seeded automatically on first run.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3, Spring Security, JWT, H2 |
| Frontend | React 18, Vite, Tailwind CSS v4, shadcn/ui, React Router, Axios |

## User Roles

| Role | Capabilities |
|------|-------------|
| **Buyer** | Browse products, add to cart, checkout (guest or member), view orders |
| **Seller** | All buyer capabilities + create, edit, and manage product listings with image upload |
| **Admin** | Full access — all seller capabilities + manage all orders and users |

## Features

- Guest checkout — no account required, just an email address
- Product image upload (sellers and admins)
- JWT authentication with role-based access control
- Paginated product catalog with category filter and search
- Multi-step checkout: address → payment
- Order tracking with status badges
- Seller dashboard for product management

## Docs

- [API Reference](docs/api.md)
- [Architecture](docs/architecture.md)
