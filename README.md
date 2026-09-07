# FinFlow

Eine Webplattform, die einem Nutzer hilft, seine finanzielle Situation zu analysieren und konkrete Handlungsmöglichkeiten zu erkennen.

**Neu hier?** → [`Docs/Einsteiger-Guide.md`](Docs/Einsteiger-Guide.md) erklärt ohne Vorkenntnisse, was FinFlow ist, wofür es gebaut wurde und wie es aufgebaut ist.

## Projektstruktur

```
FinFlow/
├── backend/            Spring Boot 4 / Java 21 – REST API (modularer Monolith)
├── frontend/           Next.js / React / TypeScript – Web-Frontend
├── docker-compose.yml  Lokale Entwicklungsumgebung (PostgreSQL)
└── Docs/
    ├── Einsteiger-Guide.md      Projekt-Einstieg ohne Vorkenntnisse
    ├── Umsetzungsvortschritt.md Aktueller Stand: erledigt / offen
    ├── Prompt/                  Sammlung der KI-Prompts, die dieses Projekt planen
    └── Word/                    Ergebnisdokumente je Entwicklungsphase (Requirements, Architektur, ...)
```

## Lokale Entwicklung

Voraussetzungen: Java 21, Maven (oder `./mvnw`), Node.js 20+, Docker.

```bash
# Datenbank starten
docker compose up -d

# Backend starten (Port 8080)
cd backend
./mvnw spring-boot:run

# Frontend starten (Port 3000)
cd frontend
npm run dev
```

Health-Check des Backends: `GET http://localhost:8080/actuator/health`

## Status

Aktueller Stand: technisches Grundgerüst (Backend-Modulstruktur, Frontend-Feature-Struktur, lokale Postgres-Umgebung). Fachliche Umsetzung folgt phasenweise gemäß `Docs/Word/FinFlow_Projektplanung_Umsetzung.docx`.
