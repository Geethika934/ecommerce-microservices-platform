# 🛒 ECommerce Microservices Platform

A production-grade, event-driven microservices backend built with **Spring Boot 3.5**, **Apache Kafka**, **Redis**, **PostgreSQL**, **MongoDB**, **Docker**, and **Kubernetes**.

---

## 🏗️ Architecture Overview

```
Client → API Gateway (8080)
              ├── Auth Service         (8081) → PostgreSQL + Redis
              ├── Order Service        (8082) → PostgreSQL + Kafka + Redis + Quartz
              ├── Product Service      (8083) → MongoDB + Kafka
              └── Notification Service (8084) → Kafka + Gmail SMTP

Event Flow:
Order Placed → Kafka (order.placed) → Notification Service → Email
Order Placed → Kafka (order.placed) → Product Service → Update Stock
```

---

## 🧩 Services

| Service | Port | Description | Tech |
|---|---|---|---|
| `api-gateway` | 8080 | Single entry point, JWT validation, routing | Spring Cloud Gateway |
| `auth-service` | 8081 | Register, Login, Logout, JWT token management | Spring Security, JWT, Redis |
| `order-service` | 8082 | Place and manage orders, Kafka producer, job scheduling | JPA, Kafka, Quartz |
| `product-service` | 8083 | Product catalog, inventory management, Kafka consumer | MongoDB, Kafka |
| `notification-service` | 8084 | Email notifications via Kafka consumer | Kafka, JavaMailSender |
| `common` | — | Shared DTOs, Kafka event models | — |

---

## ⚙️ Tech Stack

- **Backend:** Java 21, Spring Boot 3.5, Spring Security 6, Spring Cloud Gateway
- **Messaging:** Apache Kafka (event-driven communication between services)
- **Caching & Security:** Redis (JWT blocklist, refresh token store, rate limiting)
- **Databases:** PostgreSQL (users, orders), MongoDB (products, notifications)
- **Job Scheduling:** Quartz (auto-cancel unpaid orders after 30 minutes)
- **Containerization:** Docker, Docker Compose
- **Orchestration:** Kubernetes (Deployments, Services, ConfigMaps, Secrets)
- **Build Tool:** Maven (multi-module project)

---

## 🔐 Security Features

- JWT access tokens (15 min) + refresh tokens (7 days) with rotation
- Redis-backed token blocklist for instant logout revocation
- Role-based access control: `CUSTOMER`, `SELLER`, `ADMIN`
- Method-level security with `@PreAuthorize`
- BCrypt password encoding
- Stateless session management

---

## 📦 Project Structure

```
ecommerce-microservices-platform/
├── api-gateway/
├── auth-service/
├── order-service/
├── product-service/
├── notification-service/
├── common/                     # Shared DTOs and Kafka events
├── k8s/
│   ├── namespace.yaml
│   ├── configmaps/
│   ├── secrets/                # gitignored — see app-secrets.example.yaml
│   ├── deployments/
│   └── services/
├── docker-compose.yml
└── pom.xml                     # Parent POM
```

---

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.9+
- Docker Desktop
- kubectl (Docker Desktop → Settings → Kubernetes → Enable)

### 1. Clone the repository
```bash
git clone https://github.com/Geethika934/ecommerce-microservices-platform.git
cd ecommerce-microservices-platform
```

### 2. Configure each service

Copy the example config and fill in your credentials:
```bash
cp auth-service/src/main/resources/application.example.yaml \
   auth-service/src/main/resources/application.yaml
```

Fill in these placeholders:

| Placeholder | Description |
|---|---|
| `<your-db-password>` | PostgreSQL password (default: `secret`) |
| `<your-256-bit-base64-secret>` | Generate with `openssl rand -base64 32` |
| `<your-gmail-address>` | Gmail address for sending notifications |
| `<your-gmail-app-password>` | Google Account → Security → App Passwords |

### 3. Start infrastructure
```bash
docker compose up -d
```
Starts: PostgreSQL, MongoDB, Redis, Kafka, Zookeeper, Kafka UI (http://localhost:8090)

### 4. Run services in IntelliJ

Add VM option to each service run configuration:
```
-Duser.timezone=UTC
```

### 5. Access the application
```
http://localhost:8080
```

---

## 📡 API Endpoints

### Auth
```
POST /auth/register     — Register new user
POST /auth/login        — Login and get JWT tokens
POST /auth/logout       — Logout (token revoked in Redis)
POST /auth/refresh      — Refresh access token
```

### Orders (requires JWT)
```
POST /orders            — Place a new order
GET  /orders/my-orders  — Get my orders
GET  /orders/{id}       — Get order by ID
```

### Products
```
GET  /products                      — Get all products (public)
GET  /products/{id}                 — Get product by ID (public)
POST /products                      — Create product (SELLER/ADMIN only)
GET  /products/category/{category}  — Get by category (public)
```

---

## 🐳 Docker Deployment

```bash
# Build and start everything
docker compose up -d --build

# View logs
docker logs orderflow-auth-service

# Stop everything
docker compose down
```

---

## ☸️ Kubernetes Deployment

```bash
# Create namespace
kubectl apply -f k8s/namespace.yaml

# Apply ConfigMaps
kubectl apply -f k8s/configmaps/

# Create secrets
cp k8s/secrets/app-secrets.example.yaml k8s/secrets/app-secrets.yaml
# Edit with your base64-encoded values
kubectl apply -f k8s/secrets/app-secrets.yaml

# Deploy everything
kubectl apply -f k8s/deployments/
kubectl apply -f k8s/services/

# Check status
kubectl get pods -n orderflow

# Access via port-forward
kubectl port-forward -n orderflow service/api-gateway-service 8080:8080
```

---

## 📨 Kafka Topics

| Topic | Producer | Consumers |
|---|---|---|
| `order.placed` | order-service | notification-service, product-service |

Monitor at **http://localhost:8090** (Kafka UI)

---

## ⏰ Job Scheduling

Quartz runs every 5 minutes inside `order-service` and automatically cancels `PENDING` orders older than 30 minutes.

---

## 🧪 Testing with Postman

1. Register: `POST http://localhost:8080/auth/register`
2. Login: `POST http://localhost:8080/auth/login` → copy `accessToken`
3. Place order: `POST http://localhost:8080/orders` with `Authorization: Bearer <token>`
4. Check email inbox for order confirmation
5. Check Kafka UI for published events

---

## 📌 Notes

- All `application.yaml` files are gitignored — use `application.example.yaml` as template
- Kubernetes secrets are gitignored — use `app-secrets.example.yaml` as template
- Windows users: add `-Duser.timezone=UTC` to JVM options in IntelliJ

---

## 👩‍💻 Author

**Guntaka Geethika Lakshmi**
- GitHub: [@Geethika934](https://github.com/Geethika934)
- LinkedIn: [guntaka-geethika-lakshmi](https://www.linkedin.com/in/guntaka-geethika-lakshmi)
