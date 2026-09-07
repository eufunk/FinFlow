# FinFlow – Umsetzungsvortschritt

Stand: 2026-09-07 (Phase 7 abgeschlossen, noch nicht committet)

Lebendes Dokument – wird nach jeder abgeschlossenen Phase aktualisiert. Ausführliche fachliche
Ergebnisse (Requirements, Architektur, Domain Model) stehen in
`Docs/Word/FinFlow_Projektplanung_Umsetzung.docx`; hier geht es nur um den technischen
Umsetzungsstand.

## Bisher umgesetzt

### Dokumentation (`Docs/`)

- `Docs/Prompt/FinFlow_Projekt_Vorbereitung.docx` – vollständige Sammlung der 18 Planungs-Prompts
  plus Abschlusskapitel mit der 17-Phasen-Roadmap.
- `Docs/Word/FinFlow_Projektplanung_Umsetzung.docx` – Ergebnisdokumente je Phase:
  - **Phase 2** – Requirements & User Stories (Personas, Epics, Acceptance Criteria, MVP-Scope,
    Business Rules, Edge Cases)
  - **Phase 3** – Architektur (C4-Diagramme, Package Structure, Frontend-/Backend-Architektur,
    Security-Architektur, 12 ADRs)
  - **Phase 4** – Domain + Database (10 Entities, Value Objects, ER-Diagramm, vollständiges DDL,
    Flyway-Migrationsplan, Seed-Konzept)

### Backend (`backend/`)

Spring Boot **4.0.8** / Java 21, modularer Monolith, Maven. Abweichung von der ursprünglichen
Vorgabe „Spring Boot 3.x“: der offizielle Initializr bietet 3.x nicht mehr an (EOL) – mit dem
Nutzer abgestimmt, bewusst auf 4.0.8 gegangen.

**Grundgerüst:** alle 10 Module als Package-Skeleton (`api`/`application`/`domain`/
`infrastructure`), `application.yml`, 12 Flyway-Migrationen (V1–V12), Testcontainers
vorkonfiguriert.

**Phase 5 – vollständig umgesetzt**, vier vertikale Slices, jede mit Entity + Invarianten,
Repository-Port + JPA-Adapter, Application-Service, DTOs/Mapper, REST-Controller, Unit- und
MockMvc-Tests:

| Modul | Endpoints | Kern |
|---|---|---|
| Financial Profile | `GET`/`PUT /api/v1/profile` | Money-Value-Object, Profil-Invarianten |
| Transactions | `GET`/`POST /api/v1/transactions`, `GET /api/v1/categories` | Kategorie-Typ-Abgleich, Zeitraum-Filter |
| Financial Goals | `GET`/`POST /api/v1/goals` | `GoalProjectionCalculator` (Zielerreichungsdatum, 100-Jahre-Obergrenze) |
| Financial Health Engine | `GET /api/v1/financial-health` | 5 Kategorien à 20 Punkte, deterministisch, keine Blackbox |

Zusätzlich minimal (nur lesend, ohne eigene CRUD-Endpoints, da nicht Teil der Phase-5-Schritte):
- `identity` – `User`, `CurrentUserProvider` (befristeter Header-Platzhalter bis Phase 9/Security)
- `Account` (financialprofile) – für Net Worth/Diversifikation
- `InsurancePolicy` (insurance) – für die Coverage-Kategorie

**Phase 7 – Scenario Engine – vollständig umgesetzt** (`scenario`-Modul):

| Endpoint | Zweck |
|---|---|
| `POST /api/v1/scenarios` | Szenario anlegen |
| `GET /api/v1/scenarios` | Szenarien auflisten (nur Parameter, keine Berechnung) |
| `GET /api/v1/scenarios/{id}/result` | Ein Szenario berechnen |
| `GET /api/v1/scenarios/compare?ids=…` | Mehrere Szenarien vergleichen |

Kern ist `ScenarioProjectionCalculator` – reine, deterministische Monat-für-Monat-Simulation
(`capital(n) = capital(n-1) * (1 + monthlyReturn) + Beitrag`), liefert `projectedCapital`,
`yearlyDevelopment`, `totalContributions`, `investmentGrowth`, `inflationAdjustedValue` sowie
optional `goalReached`/`goalReachedDate`. Zwei bewusste Annahmen zu Lücken in Prompt 7 (dort
gefordert, aber nicht in den Eingaben spezifiziert): ein optionales `targetCapital`-Feld wurde
ergänzt, damit `goalReached` überhaupt einen Bezugspunkt hat; `monthlyIncome`/`incomeGrowth`/
`expensesGrowth` wurden als optionale Felder an das bestehende Scenario-Schema aus Phase 4
angehängt (Migration V12) statt es neu zu entwerfen.

**Tests:** 132/132 lauffähige Tests grün (reine Unit-Tests + `@WebMvcTest`-Slices), davon 13 allein
für den Scenario-Calculator inkl. aller in Prompt 7 geforderten Fälle (0 % Rendite, negative
Rendite, 0 € Sparrate, negative Werte, 100 Jahre Laufzeit, Inflation inkl. Extremfall -100 %,
nicht-terminierende Dezimalbrüche). Mehrere Testcontainers-Integrationstests kompilieren, laufen
auf dieser Maschine mangels Docker nicht – in jedem Fall verifiziert, dass der Fehler
ausschließlich am fehlenden Docker-Daemon liegt, nicht am Code.

**Beim Verifizieren gefundene und behobene echte Bugs:**
- `pom.xml`: `4.0.8.RELEASE` → `4.0.8` (Spring Boot 4 kennt kein `.RELEASE`-Suffix mehr)
- `TestcontainersConfiguration`: `PostgreSQLContainer<>` → das neue Testcontainers-Postgres-Modul
  ist nicht generisch
- Integrationstest: mehrdeutiger `save()`-Aufruf, weil zunächst gegen den konkreten
  Spring-Data-Typ statt gegen den Repository-Port programmiert wurde
- `Money` hatte kein `isPositive()`
- `GlobalExceptionHandler` hatte keinen Handler für kaputte JSON-Bodies (fiel fälschlich auf 500
  statt 400 zurück)

### Frontend (`frontend/`)

Next.js 16 / React 19 / TypeScript-Grundgerüst, TanStack Query + React Hook Form + Zod
installiert und verdrahtet (Provider, typisierter API-Client), Feature-Ordnerstruktur für alle
9 geplanten Bereiche angelegt. **Noch keine echten Seiten/Komponenten gebaut.**

### Bewusst offene Lücken (keine vergessenen Baustellen, sondern dokumentierte Scoping-Entscheidungen)

- Insurance/Account haben noch keine REST-Endpoints zum Anlegen → die beiden zugehörigen
  Health-Score-Kategorien liefern für echte Nutzer aktuell 0 Punkte.
- Kein Docker auf dieser Entwicklungsmaschine → Testcontainers-Integrationstests nie live
  gelaufen (nur Kompilierung + Fehleranalyse verifiziert).
- Security/JWT (Phase 9) noch nicht gebaut → `CurrentUserProvider` ist ein befristeter
  Header-basierter Platzhalter (`X-User-Id`).
- Transactions und Financial Goals unterstützen bewusst nur Create + Read (kein Update/Delete),
  da in keinem der Prompt-Beispiele vorgesehen.

## Was noch zu tun ist

Reihenfolge gemäß der in Prompt 1/Abschlusskapitel festgelegten Roadmap:

| Phase | Inhalt | Status |
|---|---|---|
| 1 | Product Vision | – (nie als eigenes Dokument erstellt, aber inhaltlich in Prompt 1 abgedeckt) |
| 2 | Requirements & User Stories | ✅ erledigt |
| 3 | Architecture | ✅ erledigt |
| 4 | Domain + Database | ✅ erledigt |
| 5 | Spring Boot Backend | ✅ erledigt |
| 6 | Financial Health Engine | ✅ erledigt (im Rahmen von Phase 5, Schritt 9) |
| 7 | Scenario Engine | ✅ erledigt |
| **8** | **Next.js Frontend (echte Seiten/Komponenten)** | **offen – nächster Schritt** |
| 9 | Security (JWT, Rollen, Ownership-Checks) | offen |
| 10 | JSON + XML Integration (inkl. Legacy-Adapter) | offen |
| 11 | Testing-Strategie (Playwright/E2E, Coverage-Konzept) | offen |
| 12 | Docker (Dockerfiles Backend/Frontend, vollständiges Compose) | offen |
| 13 | Kubernetes | offen |
| 14 | GitLab CI/CD | offen |
| 15 | Thymeleaf / Oracle / PL-SQL (optionale Erweiterungen) | offen |
| 16 | Dokumentation (README, ADRs pflegen, API-Doku/OpenAPI) | offen |
| 17 | Interview Preparation | offen |

### Konkrete Nacharbeiten (unabhängig von der Roadmap-Reihenfolge)

- Account/InsurancePolicy um POST-Endpoints erweitern, sobald die jeweiligen Epics dran sind
  (macht die Financial-Health-Kategorien für echte Nutzer aussagekräftig).
- Sobald Docker verfügbar ist: alle Testcontainers-Integrationstests einmal tatsächlich laufen
  lassen statt nur kompilieren.
- OpenAPI/Swagger-Dokumentation der bisherigen Endpoints (Teil von Phase 5 laut Prompt, bisher
  ausgelassen).

**Empfehlung:** weiter mit **Phase 8 – Next.js Frontend** (erste echte Seiten: Login-Platzhalter,
Dashboard, Transactions).
