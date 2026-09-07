# FinFlow – Guide für absolute Einsteiger

Dieser Guide ist für Menschen geschrieben, die **noch nie** mit diesem Projekt zu tun hatten –
egal ob Familie, Freunde, ein neuer Kollege oder du selbst in ein paar Monaten, wenn du dich
nicht mehr an alles erinnerst. Keine Vorkenntnisse in Programmierung nötig.

---

## 1. Was ist FinFlow eigentlich?

FinFlow ist eine **Webseiten-Anwendung** (so etwas wie Online-Banking oder eine Budget-App),
die einer Person helfen soll, ihre **finanzielle Situation zu verstehen**.

Stell es dir vor wie einen **Fitness-Tracker – nur für dein Geld statt für deinen Körper**:

- Du trägst ein, was du verdienst und ausgibst.
- Die App berechnet daraus eine Art „Finanz-Gesundheits-Punktzahl“ (0–100 Punkte).
- Du kannst Sparziele festlegen (z. B. „10.000 € für ein Auto bis 2028“) und siehst, ob du das
  mit deiner aktuellen Sparrate schaffst.
- Du kannst durchspielen, was passiert, wenn du mehr sparst, oder die Zinsen anders laufen
  („Was-wäre-wenn“-Simulationen).

## 2. Warum wurde FinFlow gebaut?

**Kurz gesagt: als Bewerbungs-Projekt.**

FinFlow ist kein Produkt, das gerade an echte Kunden verkauft wird. Es ist ein
**Portfolio-Projekt** – etwas, das man in einem Bewerbungsgespräch zeigen kann, um zu beweisen:
„Ich kann eine moderne, professionelle Software von vorne bis hinten selbst bauen.“

Der Anlass war eine Bewerbung als **Senior Fullstack-Entwickler (Java)** bei einem
Finanz-Technologie-Unternehmen. Deshalb deckt das Projekt absichtlich sehr viele Themen ab, die
in solchen Jobs wichtig sind: Datenbanken, Sicherheit, automatisierte Tests, Cloud-Bereitstellung
(„Deployment“) und so weiter – auch wenn eine reale Firma so ein Projekt vielleicht kleiner
anfangen würde.

## 3. Was soll die App am Ende können?

Wenn FinFlow fertig ist, soll ein Nutzer sich einloggen und ein **Dashboard** (eine
Übersichtsseite) sehen mit:

| Bereich | Was es macht (einfach erklärt) |
|---|---|
| Financial Health Score | Eine Punktzahl 0–100, die zeigt, wie gesund deine Finanzen sind |
| Einnahmen/Ausgaben | Was reinkommt und rausgeht, in Kategorien sortiert (Miete, Lebensmittel, ...) |
| Sparziele | Konkrete Ziele mit Betrag und Datum, plus Fortschrittsanzeige |
| Szenario-Simulator | „Was wäre, wenn ich 200 € mehr im Monat spare?“ |
| Versicherungsübersicht | Welche Risiken (Haftpflicht, Berufsunfähigkeit, ...) abgesichert sind |
| Berater-Ansicht | Ein Finanzberater kann (mit Erlaubnis) die Zahlen seiner Kunden einsehen |

Aktuell ist davon **die Rechen-Logik im Hintergrund fertig**, aber die eigentliche Webseite zum
Anklicken existiert noch nicht (siehe Abschnitt 7).

## 4. Wie ist das Projekt aufgebaut? (Ordnerstruktur)

Ein Software-Projekt wie dieses besteht typischerweise aus zwei großen Hälften plus etwas
Dokumentation drumherum:

```
FinFlow/
├── backend/     ← der "Motor" – rechnet, speichert, prüft Regeln (unsichtbar für den Nutzer)
├── frontend/    ← die "Karosserie" – das, was man im Browser sieht und anklickt
├── Docs/        ← alle Erklärungen, Pläne und dieser Guide hier
└── docker-compose.yml  ← eine Art "Startknopf" für die Datenbank auf dem eigenen Rechner
```

**Analogie:** Stell dir ein Auto vor. Das **Backend** ist der Motor unter der Haube – er macht
die eigentliche Arbeit, aber niemand sieht ihn direkt. Das **Frontend** ist das Lenkrad, die
Anzeigen und Knöpfe im Innenraum – das, womit der Fahrer (Nutzer) tatsächlich interagiert.

## 5. Welche Werkzeuge (Technologien) werden benutzt – und wofür?

Hier eine Übersetzung der wichtigsten Fachbegriffe, die in diesem Projekt fallen:

| Begriff | Was es ist (ganz einfach) |
|---|---|
| **Java** | Eine Programmiersprache – die "Sprache", in der der Backend-Motor geschrieben ist |
| **Spring Boot** | Ein Werkzeugkasten für Java, der viel Grundarbeit abnimmt (ähnlich wie ein Baukasten statt alles einzeln zu schnitzen) |
| **PostgreSQL** | Die Datenbank – ein riesiger, sehr ordentlicher Aktenschrank, in dem alle Daten dauerhaft gespeichert werden |
| **REST API** | Die "Sprache", mit der Frontend und Backend miteinander reden – wie eine Speisekarte mit festen Bestellmöglichkeiten |
| **Next.js / React** | Werkzeuge, um die eigentliche Webseite (das, was man sieht) zu bauen |
| **TypeScript** | Eine strengere Version von JavaScript (der "Sprache des Browsers"), die Tippfehler früher erkennt |
| **Docker** | Verpackt eine Anwendung so, dass sie auf jedem Rechner gleich läuft – wie ein Versandcontainer, der überall passt |
| **Git / GitHub** | Ein "Zeitmaschinen"-System für Code: jede Änderung wird gespeichert, man kann jederzeit zurückspringen |
| **Maven** | Verwaltet für das Java-Projekt, welche fremden Bausteine (Bibliotheken) gebraucht werden |
| **JUnit / Mockito** | Werkzeuge für automatisierte Tests – die Software prüft sich quasi selbst, ob sie noch richtig rechnet |
| **JWT / Security** | Der Türsteher-Mechanismus, der später prüft, wer sich einloggen und was er sehen darf (noch nicht gebaut) |

Du musst nichts davon im Detail verstehen – wichtig ist nur: **jedes Werkzeug hat eine klare
Aufgabe**, niemand hat sich das willkürlich ausgesucht (die Gründe dafür stehen, falls es dich
interessiert, in `Docs/Word/FinFlow_Projektplanung_Umsetzung.docx`, Kapitel „Architektur“, als
sogenannte „Architecture Decision Records“).

## 6. Wie hängt das alles zusammen?

Ganz vereinfacht, wenn ein Nutzer später etwas in der Webseite anklickt:

```
[Browser/Handy]  →  [Frontend]  →  Anfrage übers Internet  →  [Backend]  →  [Datenbank]
     (Anzeige)         (Next.js)                                (Spring Boot)   (PostgreSQL)
```

1. Der Nutzer klickt z. B. auf „Transaktion hinzufügen“.
2. Das Frontend schickt diese Information ans Backend.
3. Das Backend prüft die Regeln (z. B. „Betrag darf nicht negativ sein“) und speichert die
   Transaktion in der Datenbank.
4. Das Backend meldet dem Frontend „erledigt“, das Frontend zeigt die Bestätigung an.

Das Backend selbst ist intern in **10 klar getrennte Abteilungen** („Module“) aufgeteilt – jedes
Modul kümmert sich nur um ein Thema (z. B. „Transactions“ nur um Buchungen, „Goals“ nur um
Sparziele). Das ist wie in einer Firma mit getrennten Abteilungen statt einem Chaos-Großraumbüro,
in dem jeder alles macht.

## 7. Was ist schon fertig – was noch nicht?

**Fertig (und automatisch getestet):**
- Der komplette Rechen-„Motor“ im Backend: Profil-Daten, Transaktionen erfassen, Sparziele
  samt Erreichungsdatum-Berechnung, und die zentrale „Financial Health Score“-Berechnung.
- Alles ist über sogenannte **Endpoints** ansprechbar – das sind feste "Adressen", über die man
  mit dem Backend reden kann (aktuell nur technisch testbar, noch keine hübsche Oberfläche).

**Noch nicht fertig:**
- Die eigentliche, klickbare Webseite (Frontend) – es gibt nur ein leeres Grundgerüst.
- Login/Sicherheit – aktuell kann sich noch niemand "echt" einloggen.
- Die Szenario-Simulation, Versicherungs-Verwaltung als eigene Seite, Datenimport, Berichte,
  Docker-Container für die fertige App, Cloud-Bereitstellung.

Die **vollständige, aktuelle Liste** mit allem Erledigten und Offenen steht in
[`Docs/Umsetzungsvortschritt.md`](Umsetzungsvortschritt.md) – das ist quasi die
Fortschritts-Checkliste des Projekts.

## 8. Wo finde ich was? (Dokumenten-Wegweiser)

| Datei/Ordner | Was du dort findest |
|---|---|
| **Diese Datei** | Der Einstieg für absolute Anfänger – du bist hier |
| `README.md` (im Hauptordner) | Kurze technische Anleitung, wie man das Projekt startet |
| `Docs/Umsetzungsvortschritt.md` | Aktueller Stand: was ist fertig, was fehlt noch |
| `Docs/Prompt/FinFlow_Projekt_Vorbereitung.docx` | Die ursprünglichen Bau-Anleitungen/Pläne für das Projekt |
| `Docs/Word/FinFlow_Projektplanung_Umsetzung.docx` | Die detaillierten fachlichen Entscheidungen (Anforderungen, Architektur, Datenmodell) |

## 9. Kleines Glossar (Fachbegriffe von A–Z)

- **API** – eine feste "Schnittstelle", über die zwei Programme miteinander reden können.
- **Backend** – der unsichtbare "Motor" im Hintergrund, der Daten speichert und berechnet.
- **Datenbank** – der digitale Aktenschrank, in dem alle Informationen dauerhaft liegen.
- **Endpoint** – eine konkrete "Adresse", über die man eine bestimmte Funktion im Backend
  anspricht (z. B. „gib mir alle Transaktionen“).
- **Entity** – ein "Ding", das die Software kennt und speichert, z. B. ein Nutzer, eine
  Transaktion, ein Sparziel.
- **Frontend** – der sichtbare Teil, den man im Browser anklickt.
- **Framework** – ein Baukasten mit vorgefertigten Bausteinen, der Entwicklern viel
  Grundarbeit abnimmt.
- **Modul** – ein klar abgegrenzter Themenbereich innerhalb der Software (wie eine Abteilung).
- **Repository** (im Code-Sinn) – der Teil, der weiß, wie man Daten in der Datenbank
  speichert/liest. (Achtung: „Repository“ meint bei GitHub etwas anderes – dort ist es der
  ganze Projekt-Ordner mit seiner Versionsgeschichte.)
- **Test** – automatisch laufender Code, der prüft, ob ein anderer Teil der Software noch
  richtig funktioniert.
- **Value Object** – ein kleiner, in sich geschlossener Wert mit eigenen Regeln, z. B. „Geldbetrag“
  (darf z. B. nie unsinnige Nachkommastellen haben).

---

**Nächster Schritt für dich als Einsteiger:** Lies README.md, wenn du das Projekt tatsächlich
auf deinem Rechner starten willst, oder wirf einen Blick in
`Docs/Umsetzungsvortschritt.md`, um zu sehen, woran gerade weitergearbeitet wird.
