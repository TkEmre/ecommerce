# Setup Guide

## Requirements

| Tool | Minimum version | Check |
|------|----------------|-------|
| Java | 17 | `java -version` |
| Node.js | 18 | `node -v` |
| npm | 9 | `npm -v` |

Maven is bundled — no separate installation needed.

---

## 1. Clone & open

```bash
git clone <repo-url>
cd e-commerce
```

---

## 2. Backend

```bash
cd backend
```

**Windows (PowerShell)**
```powershell
.\mvnw spring-boot:run
```

**Windows (Command Prompt)**
```cmd
mvnw spring-boot:run
```

**macOS / Linux**
```bash
./mvnw spring-boot:run
```

The first run downloads dependencies and may take a few minutes.

### What happens on first run

- H2 database is created at `backend/data/ecommerce_db`
- `DataInitializer` seeds the database:
  - Admin account: `admin@store.com` / `admin123`
  - 15 sample products across 4 categories

### Verify

| URL | Expected |
|-----|---------|
| `http://localhost:8080/api/v1/products` | JSON product list |
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/h2-console` | H2 DB console (sa / password) |

---

## 3. Frontend

Open a **new terminal**, then:

```bash
cd frontend
npm install
npm run dev
```

App → `http://localhost:5173`

> The backend must be running for the frontend to work.

---

## 4. Default credentials

| Email | Password | Role |
|-------|----------|------|
| `admin@store.com` | `admin123` | Admin |

---

## Resetting the database

Stop the backend, delete the database files, then restart:

**Windows (PowerShell)**
```powershell
Remove-Item "backend\data\*" -Force
```

**Windows (Command Prompt)**
```cmd
del /q "backend\data\*"
```

**macOS / Linux**
```bash
rm -f backend/data/*
```

The database and seed data are recreated automatically on the next startup.

---

## Environment

| Variable | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Backend port (application.properties) |
| `jwt.secret` | (set in properties) | Change before deploying to production |
| `jwt.expiration` | `86400000` (24h) | Token lifetime in milliseconds |

---

## Production notes

- Replace H2 with PostgreSQL or MySQL by updating `pom.xml` and `application.properties`
- Change `jwt.secret` to a strong random value
- Set `spring.jpa.hibernate.ddl-auto=validate` (not `update`) in production
- Serve uploaded images from object storage (S3, etc.) instead of the local `uploads/` directory
- Build the frontend with `npm run build` and serve `dist/` via a reverse proxy (nginx) pointing to the backend
