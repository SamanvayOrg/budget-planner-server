# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

The Spring Boot REST API + PostgreSQL backend for **citybudgets.in** — a municipal (ULB) budget-planning app built for CEPT by Samanvay Foundation. This is one of three sibling repos:

- `budget-planner-server` — this API (Java 11 / Spring Boot 2.6.7)
- `../budget-planner-web` — React (CRA) single-page frontend
- `../budget-planner-infra` — Ansible provisioning & deployment

Runtime: browser → Nginx → this API on `:8080` → PostgreSQL. **Auth0** is the identity provider; this service is an OAuth2 resource server that validates Auth0-issued JWTs.

## Commands

| Task | Command |
|------|---------|
| Build (skip tests) | `./gradlew clean build -x test` (= `make build-server`) |
| Build + run tests | `./gradlew clean build` (= `make test-server`) |
| Run all tests | `./gradlew test` (JUnit 5 / `useJUnitPlatform`) |
| Run a single test | `./gradlew test --tests org.mbs.budgetplannerserver.BudgetControllerTest` |
| Run locally | `make start` (`./gradlew bootRun`) — app on port **8080** |
| Run against local DB on :5442 | `make start-with-local-db` |
| Run against tunneled remote DB on :6015 | `make start-with-remote-db dbUser=... dbPassword=...` |
| Debug (JDWP `:5005`) | `make debug-with-local-db` or `make debug-with-remote-db` |
| Tunnel to prod (Azure) DB | `make tunnel-prod-db` (SSH `-L 6015:cwas-cm-sql.postgres.database.azure.com:5432`) |

DB connection is env-driven: `BPS_DATASOURCE` (default `jdbc:postgresql://localhost:5432/budget`), `BPS_DB_USER` (`budget_user`), `BPS_DB_PASSWORD`. Auth0 config: `BPS_CLIENT_ID`/`BPS_CLIENT_SECRET`, `BPS_AUTH_MGT_CLIENT_ID`/`BPS_AUTH_MGT_CLIENT_SECRET`, `BPS_AUTH_ISSUER_URI` (default `https://budget-planner.us.auth0.com`). See `src/main/resources/application.yaml`; the `production` profile (set on deploy) turns off `show-sql` and logs to `/var/log/budget-planner/`.

Flyway migrations live in `src/main/resources/db/migration/` (`V1.x__*.sql`) and **run automatically on startup** — add a new versioned file rather than editing existing ones. CI is CircleCI (`.circleci/config.yml`): build, staging auto-deploy on `main`, prod behind manual approval.

## Architecture

Conventional layered Spring layout, root package `org.mbs.budgetplannerserver`: `controller/` → `service/` → `repository/` (Spring Data `CrudRepository`) → `domain/` entities. API DTOs live in `contract/` and convert via `mapper/` (e.g. `BudgetContractMapper`). Excel export is in `export/service/BudgetExcelReportService.java` (Apache POI). Security in `security/`.

The non-obvious parts worth understanding before changing anything:

### Domain model
- A **`Budget`** is unique per `(financialYear, municipality)` and owns many **`BudgetLine`** (cascade). A `BudgetLine` carries three amount tiers: budgeted (`budgetedAmount`), revised (`revisedAmount`), and actuals (`actualAmount`, plus year-N-1's `eightMonthActualAmount` and `fourMonthProbableAmount`). It also has a `displayOrder`.
- **Budget-head codes** form a 4-level hierarchy under `domain/code/`: `MajorHeadGroup` → `MajorHead` → `MinorHead` → `DetailedHead`, with an orthogonal `FunctionGroup` → `Function`. Each `BudgetLine` references a `Function` + a `DetailedHead`.
- The four `MajorHeadGroup`s ("Revenue Receipt", "Expenses", "Assets (Capital Expenditure)", "Liability (Capital Income)") drive the closing-balance calc in `BudgetContractMapper`: `closing = opening + revenueReceipt + capitalExpenditure − expenses − capitalIncome`. Watch the naming — the capital groups are labelled by their balance-sheet side ("Assets…", "Liability…") but referred to in code as `capitalExpenditure` / `capitalIncome`.
- Org hierarchy: `State` → `Municipality` (ULB) → `User` and `Budget`; `CityClass` classifies municipalities.

### Year-over-year comparison
Budgets display up to 4 prior years side by side. `PreviousYearBudgets` + `BudgetLineDetail` (a value object keyed by function/detailed-head/name) match equivalent lines across years; `NullBudget` is a Null-Object returned when a prior year is missing. **The previous-year lookups are encapsulated as methods on `BudgetLine`** (`getPreviousActuals/Budgeted/EightMonthActuals/FourMonthProbables`) — keep that logic on the entity rather than scattering it into mappers/services.

### Soft delete
All entities extend `BaseModel`, which uses Hibernate `@SQLDelete` + `@Where(clause="is_voided=false")`. Deleting an entity sets `is_voided=true`; every query auto-filters voided rows. Don't hard-delete.

### Status workflow
`BudgetStatus` is a state machine (Draft → SubmittedToGB → ApprovedByGB → ApprovedByDistrict) guarded by `isTransitionAllowed()`; transitions are recorded in `BudgetStatusAudit`.

### Auth & tenancy
`security/SecurityConfig` configures the OAuth2 resource server: JWTs are validated against the Auth0 issuer and the custom `AudienceValidator` (audience `https://api.budget-planner`). The token's `permissions` claim becomes Spring authorities used in `@PreAuthorize` — the four in use are `read`, `write`, `admin`, `superAdmin`. `service/Auth0Service` calls the Auth0 **Management API** to provision users, assign roles, and trigger password-reset emails. Multi-tenancy is implicit: the current `User` (looked up by Auth0 `userName` from the security context) belongs to one `Municipality`, and budget queries filter by that municipality.

Main controllers: `BudgetController` (`/api/budget*`, `/api/budgets`), `ReportController`/`ReportsController`, `UserController`, `MunicipalityController`, `StateController`, `CityClassController`, `MetadataController`, `TranslationController`.
