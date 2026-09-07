"use client";

import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { LineChart } from "@/components/ui/LineChart";
import { LoadingState } from "@/components/ui/LoadingState";
import { formatCurrency } from "@/lib/format";
import { useScenarioResult } from "@/features/scenarios/api/useScenarioResult";
import { useScenarios } from "@/features/scenarios/api/useScenarios";

export function WealthDevelopmentCard() {
  const scenariosQuery = useScenarios();
  const firstScenario = scenariosQuery.data?.[0];
  const resultQuery = useScenarioResult(firstScenario?.id);

  return (
    <Card>
      <p className="text-sm font-medium text-foreground">Projizierte Vermögensentwicklung</p>

      {scenariosQuery.isLoading && (
        <div className="mt-4">
          <LoadingState label="Szenarien werden geladen …" />
        </div>
      )}

      {!scenariosQuery.isLoading && !firstScenario && (
        <div className="mt-4">
          <EmptyState
            title="Noch kein Szenario angelegt"
            description="Lege ein Szenario an, um eine Vermögensprognose über die Laufzeit zu sehen."
          />
        </div>
      )}

      {firstScenario && (
        <>
          <p className="text-xs text-muted">Basierend auf Szenario „{firstScenario.name}“</p>
          <div className="mt-4">
            {resultQuery.isLoading && <LoadingState label="Berechnung läuft …" />}
            {resultQuery.data && (
              <>
                <LineChart
                  points={resultQuery.data.yearlyDevelopment.map((snapshot) => ({
                    label: `Jahr ${snapshot.year}`,
                    value: snapshot.capital,
                  }))}
                />
                <p className="mt-3 text-sm text-muted">
                  Prognostiziertes Kapital nach {firstScenario.durationInYears} Jahren:{" "}
                  <span className="font-medium text-foreground">
                    {formatCurrency(resultQuery.data.projectedCapital)}
                  </span>
                </p>
              </>
            )}
          </div>
        </>
      )}
    </Card>
  );
}
