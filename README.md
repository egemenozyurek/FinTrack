# FinTrack — Personal Finance Tracker

A full-stack mobile application for tracking income/expenses, managing debts, and generating monthly financial reports.

**Backend:** Java 25 · Spring Boot 4 · PostgreSQL · JWT  
**Frontend:** React Native · Expo *(in development)*

---

## Features

- **Income & Expense Tracking** — category-based records, recurring transaction support
- **Debt Management** — lent/borrowed tracking, partial payment history, due date reminders
- **Monthly Reports** — income/expense summary, category breakdown, 6-month trend analysis
- **JWT Authentication** — secure register and login
- **RESTful API** — 15+ endpoints documented with Swagger UI

---

## Technology Decisions

| Decision | Why |
|---|---|
| `BigDecimal` for money | `double` causes floating point errors; mandatory for financial apps |
| `FetchType.LAZY` | Prevents unnecessary JOINs and N+1 query problem |
| Flyway migration | Schema changes are version-controlled; `ddl-auto=create` risks data loss in production |
| JWT stateless auth | More practical than sessions for mobile clients |
| Interface + Impl separation | Decouples dependency to abstraction; simplifies unit testing |
| DTO layer | Prevents exposing entities directly; sensitive fields like `passwordHash` never reach the response |

---

## Project Structure

```
src/main/java/com/profileinsight/fintrack/
├── config/          # SecurityConfig, SwaggerConfig
├── controller/      # REST endpoints
├── dto/             # Request / Response objects
│   ├── request/
│   └── response/
├── entity/          # JPA entity classes
├── enums/           # TransactionType, DebtType, DebtStatus
├── exception/       # GlobalExceptionHandler, custom exceptions
├── repository/      # Spring Data JPA interfaces
├── security/        # JWT filter, UserDetailsService
└── service/         # Business logic
    └── impl/
```

---

## API Endpoints

### Auth
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user | ❌ |
| POST | `/api/v1/auth/login` | Login — returns JWT token | ❌ |

### Transactions
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/transactions` | Add transaction | ✅ |
| GET | `/api/v1/transactions` | List all transactions | ✅ |
| GET | `/api/v1/transactions/{id}` | Get single transaction | ✅ |
| GET | `/api/v1/transactions/monthly?year=&month=` | Filter by month | ✅ |
| PUT | `/api/v1/transactions/{id}` | Update transaction | ✅ |
| DELETE | `/api/v1/transactions/{id}` | Delete transaction | ✅ |

### Debts
| Method | URL | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/debts` | Add debt | ✅ |
| GET | `/api/v1/debts` | List all debts | ✅ |
| GET | `/api/v1/debts/overdue` | Get overdue debts | ✅ |
| POST | `/api/v1/debts/{id}/payments` | Add payment | ✅ |
| DELETE | `/api/v1/debts/{id}` | Delete debt | ✅ |

### Categories & Reports
| Method | URL | Description | Auth |
|---|---|---|---|
| GET | `/api/v1/categories` | List all categories | ✅ |
| GET | `/api/v1/categories?type=EXPENSE` | Filter by type | ✅ |
| GET | `/api/v1/reports/monthly?year=&month=` | Monthly report | ✅ |

---

## Getting Started

### Prerequisites
- Java 25+
- PostgreSQL 15+
- Maven 3.9+

### 1. Create Database

```sql
CREATE DATABASE fintrack;
```

### 2. Configure Application

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fintrack
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

jwt.secret=at-least-32-characters-long-secret-key
jwt.expiration=86400000
```

### 3. Run

```bash
mvn clean package
mvn spring-boot:run
```

The application starts at `http://localhost:8080`.  
Flyway automatically creates all tables and seeds 14 default categories on first run.

---

## API Usage

**1. Register:**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'
```

**2. Use the returned token in subsequent requests:**
```bash
curl -X GET http://localhost:8080/api/v1/categories \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
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

- [x] Backend API (Spring Boot + PostgreSQL)
- [x] JWT authentication
- [x] Flyway migration + seed data
- [ ] React Native frontend (Expo)
- [ ] Docker Compose
- [ ] FinTrack Analytics (Python + scikit-learn)

---

## License

MIT
