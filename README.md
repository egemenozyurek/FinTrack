# FinTrack — Backend

REST API for a full-stack personal finance tracker. Handles authentication, transaction management, debt tracking and monthly financial reporting.

Built with Java Spring Boot 4, deployed on Railway.

**Mobile frontend:** [github.com/egemenozyurek/FinTrack-Mobile](https://github.com/egemenozyurek/FinTrack-Mobile)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| Migrations | Flyway |
| ORM | Spring Data JPA / Hibernate |
| Containerization | Docker + Docker Compose |
| Deploy | Railway |
| Documentation | Swagger / OpenAPI |
| Build | Maven |

---

## Architecture

```
src/main/java/com/profileinsight/fintrack/
├── config/          # SecurityConfig, SwaggerConfig, FlywayConfig
├── controller/      # REST endpoints — HTTP layer only
├── dto/             # Request / Response objects
│   ├── request/
│   └── response/
├── entity/          # JPA entity classes
├── enums/           # TransactionType, DebtType, DebtStatus
├── exception/       # GlobalExceptionHandler, custom exceptions
├── repository/      # Spring Data JPA interfaces + JPQL queries
├── security/        # JWT filter, UserDetailsService
└── service/         # Business logic
    └── impl/
```

---

## API Endpoints

### Auth
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user | Public |
| POST | `/api/v1/auth/login` | Login — returns JWT token | Public |

### Transactions
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/transactions` | Add transaction | Required |
| GET | `/api/v1/transactions` | List all transactions | Required |
| GET | `/api/v1/transactions/{id}` | Get single transaction | Required |
| GET | `/api/v1/transactions/monthly?year=&month=` | Filter by month | Required |
| PUT | `/api/v1/transactions/{id}` | Update transaction | Required |
| DELETE | `/api/v1/transactions/{id}` | Delete transaction | Required |

### Debts
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/debts` | Add debt | Required |
| GET | `/api/v1/debts` | List all debts | Required |
| GET | `/api/v1/debts/overdue` | Get overdue debts | Required |
| POST | `/api/v1/debts/{id}/payments` | Add payment | Required |
| DELETE | `/api/v1/debts/{id}` | Delete debt | Required |

### Categories & Reports
| Method | URL | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/categories` | List all categories | Required |
| GET | `/api/v1/categories?type=EXPENSE` | Filter by type | Required |
| GET | `/api/v1/reports/monthly?year=&month=` | Monthly report | Required |

---

## Key Design Decisions

| Decision | Why |
|---|---|
| `BigDecimal` for money | `double` causes floating point errors — unacceptable in financial apps |
| `FetchType.LAZY` | Prevents unnecessary JOINs and N+1 query problem |
| Flyway over `ddl-auto=create` | Schema changes are version-controlled; `create` risks data loss in production |
| JWT stateless auth | More practical than sessions for mobile clients |
| Interface + Impl separation | Decouples dependencies; simplifies unit testing with Mockito |
| DTO layer | Prevents exposing entities; sensitive fields like `passwordHash` never reach the response |
| `GlobalExceptionHandler` | Centralised error handling — no try/catch in controllers |

---

## Getting Started

### Prerequisites
- Java 21+
- PostgreSQL 15+
- Maven 3.9+

### 1. Create Database

```sql
CREATE DATABASE fintrack;
```

### 2. Configure

Copy `.env.example` to `.env` and fill in your values:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/fintrack
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=at-least-32-characters-long-secret-key
JWT_EXPIRATION=86400000
```

### 3. Run

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

The application starts at `http://localhost:8080`.
Flyway automatically creates all tables and seeds 14 default categories on first run.

### Docker

```bash
docker compose up --build
```

---

## Live API

```
https://fintrack-production-2a5c.up.railway.app
```

---

## Database Schema

```
users ──< transactions >── categories
  │
  └──< debts ──< debt_payments
```

---

## Roadmap

- [x] REST API with Spring Boot
- [x] JWT authentication
- [x] PostgreSQL + Flyway migration + seed data
- [x] Docker + Docker Compose
- [x] Deployed on Railway
- [ ] Unit tests (JUnit + Mockito)
- [ ] FinTrack Analytics (Python + scikit-learn)

---

## License

MIT
