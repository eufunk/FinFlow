"use client";

import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { LineChart } from "@/components/ui/LineChart";
import { LoadingState } from "@/components/ui/LoadingState";
import { formatCurrency, formatDate, formatPercent } from "@/lib/format";
import { useScenarioResult } from "../api/useScenarioResult";
import type { Scenario } from "../types/scenario";

interface ScenarioCardProps {
  scenario: Scenario;
  selected: boolean;
  onToggleSelected: () => void;
}

export function ScenarioCard({ scenario, selected, onToggleSelected }: ScenarioCardProps) {
  const resultQuery = useScenarioResult(scenario.id);

  return (
    <Card>
      <div className="flex items-start justify-between gap-2">
        <div>
          <p className="font-medium text-foreground">{scenario.name}</p>
          <p className="mt-0.5 text-xs text-muted">
            {formatCurrency(scenario.currentCapital)} Start · {formatCurrency(scenario.monthlySavings)}/Monat ·{" "}
            {formatPercent(scenario.annualReturn)} Rendite · {scenario.durationInYears} Jahre
          </p>
        </div>
        <label className="flex shrink-0 items-center gap-1.5 text-xs text-muted">
          <input
            type="checkbox"
            checked={selected}
            onChange={onToggleSelected}
            className="h-4 w-4 rounded border-border"
          />
          Vergleichen
        </label>
      </div>

      <div className="mt-4">
        {resultQuery.isLoading && <LoadingState label="Berechnung läuft …" />}
        {resultQuery.data && (
          <>
            <LineChart
              points={resultQuery.data.yearlyDevelopment.map((snapshot) => ({
                label: `Jahr ${snapshot.year}`,
                value: snapshot.capital,
              }))}
              height={100}
            />
            <div className="mt-3 flex items-center justify-between text-sm">
              <span className="text-muted">Prognostiziertes Kapital</span>
              <span className="font-medium text-foreground">{formatCurrency(resultQuery.data.projectedCapital)}</span>
            </div>
            {resultQuery.data.goalReached && resultQuery.data.goalReachedDate && (
              <div className="mt-2">
                <Badge variant="positive">Ziel erreicht am {formatDate(resultQuery.data.goalReachedDate)}</Badge>
              </div>
            )}
          </>
        )}
      </div>
    </Card>
  );
}
