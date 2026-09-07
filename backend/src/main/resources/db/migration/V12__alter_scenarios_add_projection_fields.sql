-- Ergänzt das Scenario-Schema aus V9 um Felder für die Scenario Engine (Prompt 7):
-- Einkommens-/Ausgabenwachstum für eine realistischere Sparraten-Projektion, sowie ein
-- optionales Zielkapital für die goalReached/goalReachedDate-Ausgabe (in Prompt 7 als Output
-- gefordert, aber in den dortigen Eingaben nicht spezifiziert - bewusste Ergänzung, siehe ADR).
ALTER TABLE scenarios
    ADD COLUMN monthly_income NUMERIC(19, 2),
    ADD COLUMN income_growth NUMERIC(6, 4),
    ADD COLUMN expenses_growth NUMERIC(6, 4),
    ADD COLUMN target_capital NUMERIC(19, 2);
