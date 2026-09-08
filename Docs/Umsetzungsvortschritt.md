# FinFlow – Umsetzungsvortschritt

Stand: 2026-09-07 (Phase 8 - Dashboard - abgeschlossen, noch nicht committet)

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

Zusätzlich minimal (nur lesend, ohne eigene Anlege-Endpoints, da nicht Teil der Phase-5-Schritte):
- `identity` – `User`, `CurrentUserProvider` (befristeter Header-Platzhalter bis Phase 9/Security)
- `Account` (financialprofile) – für Net Worth/Diversifikation, seit Phase 8 zusätzlich mit
  `GET /api/v1/accounts` (fürs Dashboard, weiterhin kein POST)
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

### Backend: Dev-Seed (Ersatz für Login bis Phase 9)

Da es noch keinen Registrierungs-/Login-Endpoint gibt, legt `DevDataSeeder` (nur unter
Spring-Profil `dev`, idempotent) beim Start einen festen Demo-User mit realistischen Beispieldaten
an: Financial Profile, 3 Accounts, 2 von 3 Kernrisiken versichert (bewusst nicht alle, für einen
realistischeren Demo-Score), ~18 Transaktionen über 3 Monate, ein Sparziel, zwei Szenarien.
`GET /api/v1/dev/demo-user` (ebenfalls nur unter `dev`) liefert dem Frontend dessen ID.

### Frontend (`frontend/`)

Next.js 16 / React 19 / TypeScript, TanStack Query + React Hook Form + Zod. **Phase 8 (erste
Seiten) begonnen:**

- Tailwind CSS ergänzt (in der Grundgerüst-Phase bewusst ausgelassen, jetzt für den geforderten
  "professionellen SaaS-Look" nachgeholt) mit einem kleinen Fintech-Farbsystem.
- Temporärer Auth-Ersatz (`src/lib/current-user.ts`): löst beim App-Start einmalig die
  Demo-User-ID über `/api/v1/dev/demo-user` auf und hängt sie als `X-User-Id`-Header an jeden
  Request – spiegelt den `CurrentUserProvider`-Platzhalter im Backend, wird in Phase 9 zusammen
  mit diesem ersetzt.
- Wiederverwendbare UI-Bausteine (`src/components/ui/`): Card, MetricCard, Badge, ProgressBar,
  Alert, EmptyState, LoadingState, ErrorState, eine handgeschriebene SVG-LineChart (keine
  Chart-Bibliothek für eine einzelne Linie).
- Navigation + App-Layout für alle 9 Seiten; **Dashboard vollständig fertig** (Financial Health
  Score mit Kategorie-Aufschlüsselung, Net Worth, Einkommen/Ausgaben/Sparquote, Empfehlungen,
  Sparziele-Zusammenfassung, projizierte Vermögensentwicklung aus dem ersten Szenario).
- **Transactions-Seite vollständig fertig**: Liste (sortiert, Kategorie-Namen aufgelöst, farblich
  nach Einnahme/Ausgabe) über eine neue `DataTable`-Komponente, „+ Neue Transaktion“ öffnet ein
  `Modal` mit einem React-Hook-Form/Zod-Formular (Kategorie-Auswahl folgt dem gewählten Typ,
  clientseitige Validierung spiegelt die Backend-Invarianten: Betrag > 0, Datum nicht in der
  Zukunft). Erfolgreiches Anlegen invalidiert den TanStack-Query-Cache, die Liste aktualisiert
  sich automatisch.
- **Goals-Seite vollständig fertig**: Kartenraster mit Fortschrittsbalken, „Erreicht“-Badge,
  monatlicher Sparrate, erwarteter Rendite, Zieldatum und voraussichtlichem Erreichungsdatum
  (bzw. „mit aktueller Sparrate nicht erreichbar“). „+ Neues Sparziel“ öffnet ein Formular
  (React Hook Form/Zod), Rendite-Eingabe erfolgt nutzerfreundlich in Prozent und wird beim
  Absenden in den vom Backend erwarteten Bruchteil umgerechnet.
- **Scenarios-Seite vollständig fertig**: Kartenraster, jede Karte lädt ihr eigenes Ergebnis
  (Mini-Chart, prognostiziertes Kapital, „Ziel erreicht“-Badge falls `targetCapital` gesetzt ist).
  Checkbox „Vergleichen“ pro Karte; ab zwei ausgewählten Szenarien erscheint ein Vergleichsblock
  mit einer neuen `ComparisonLineChart`-Komponente (mehrere Linien + Legende) sowie
  Kennzahlen-Karten je Szenario (prognostiziertes Kapital, kaufkraftbereinigter Wert,
  Zielerreichungsdatum). „+ Neues Szenario“ öffnet ein Formular; Rendite/Inflation werden wie bei
  Goals nutzerfreundlich in Prozent eingegeben.
- Die übrigen 4 Seiten (Insurance, Reports, Settings, Admin) sind bewusst transparente „Coming
  soon“-Platzhalter statt 404 – jeweils mit Hinweis, was am Backend schon existiert.
- `npm run build` und `npm run lint` laufen fehlerfrei; Dev-Server manuell gegen alle Routen
  geprüft (kein Backend/DB in dieser Umgebung verfügbar, daher keine echten Daten sichtbar –
  Lade-/Fehlerzustand wurde stattdessen verifiziert).

### Bewusst offene Lücken (keine vergessenen Baustellen, sondern dokumentierte Scoping-Entscheidungen)

- Insurance/Account haben weiterhin keine REST-Endpoints zum Anlegen (nur Dev-Seed bzw. interner
  Lesezugriff) → ohne den Dev-Seed liefern die beiden zugehörigen Health-Score-Kategorien für
  echte Nutzer 0 Punkte.
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
| **8** | **Next.js Frontend (echte Seiten/Komponenten)** | **🟡 teilweise – Dashboard, Transactions, Goals, Scenarios fertig, 4 Seiten offen** |
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

**Empfehlung:** weiter mit **Phase 8 – Settings-Seite** (Finanzprofil bearbeiten, API `GET`/
`PUT /api/v1/profile` bereits vollständig vorhanden). Die verbleibenden Seiten Insurance, Reports
und Admin brauchen erst zusätzliche Backend-Arbeit (eigene CRUD-Endpoints bzw. Security/Rollen aus
Phase 9), bevor sie sinnvoll gebaut werden können.
