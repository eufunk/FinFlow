"use client";

import { useState } from "react";
import { Card } from "@/components/ui/Card";
import { ComparisonLineChart, type ComparisonSeries } from "@/components/ui/ComparisonLineChart";
import { EmptyState } from "@/components/ui/EmptyState";
import { ErrorState } from "@/components/ui/ErrorState";
import { LoadingState } from "@/components/ui/LoadingState";
import { Modal } from "@/components/ui/Modal";
import { formatCurrency, formatDate } from "@/lib/format";
import { useCompareScenarios } from "@/features/scenarios/api/useCompareScenarios";
import { useScenarios } from "@/features/scenarios/api/useScenarios";
import { ScenarioCard } from "@/features/scenarios/components/ScenarioCard";
import { ScenarioForm } from "@/features/scenarios/components/ScenarioForm";

const SERIES_COLORS = ["#2563eb", "#16a34a", "#d97706", "#dc2626", "#7c3aed"];

export default function ScenariosPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  const scenariosQuery = useScenarios();
  const compareQuery = useCompareScenarios(selectedIds);

  function toggleSelected(id: string) {
    setSelectedIds((previous) => (previous.includes(id) ? previous.filter((x) => x !== id) : [...previous, id]));
  }

  const comparisonSeries: ComparisonSeries[] = (compareQuery.data ?? []).map((result, index) => ({
    id: result.scenarioId,
    label: result.scenarioName,
    color: SERIES_COLORS[index % SERIES_COLORS.length],
    points: result.yearlyDevelopment.map((snapshot) => ({ year: snapshot.year, value: snapshot.capital })),
  }));

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Szenarien</h1>
          <p className="mt-1 text-sm text-muted">
            Simuliere finanzielle Zukunftsszenarien und vergleiche sie miteinander.
          </p>
        </div>
        <button
          type="button"
          onClick={() => setModalOpen(true)}
          className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
        >
          + Neues Szenario
        </button>
      </div>

      {scenariosQuery.isLoading && <LoadingState label="Szenarien werden geladen …" />}
      {scenariosQuery.isError && <ErrorState message="Szenarien konnten nicht geladen werden." />}
      {scenariosQuery.data && scenariosQuery.data.length === 0 && (
        <EmptyState title="Noch keine Szenarien" description="Lege dein erstes Szenario über den Button oben an." />
      )}

      {scenariosQuery.data && scenariosQuery.data.length > 0 && (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          {scenariosQuery.data.map((scenario) => (
            <ScenarioCard
              key={scenario.id}
              scenario={scenario}
              selected={selectedIds.includes(scenario.id)}
              onToggleSelected={() => toggleSelected(scenario.id)}
            />
          ))}
        </div>
      )}

      {selectedIds.length === 1 && (
        <p className="text-xs text-muted">Wähle mindestens ein zweites Szenario aus, um einen Vergleich zu sehen.</p>
      )}

      {selectedIds.length >= 2 && (
        <Card>
          <p className="text-sm font-medium text-foreground">Vergleich: Vermögensentwicklung</p>
          {compareQuery.isLoading && <LoadingState label="Vergleich wird berechnet …" />}
          {compareQuery.data && (
            <div className="mt-4">
              <ComparisonLineChart series={comparisonSeries} />
              <div className="mt-4 grid grid-cols-1 gap-3 sm:grid-cols-2">
                {compareQuery.data.map((result, index) => (
                  <div key={result.scenarioId} className="rounded-lg border border-border p-3 text-sm">
                    <p className="flex items-center gap-1.5 font-medium text-foreground">
                      <span
                        className="h-2 w-2 rounded-full"
                        style={{ backgroundColor: SERIES_COLORS[index % SERIES_COLORS.length] }}
                      />
                      {result.scenarioName}
                    </p>
                    <p className="mt-1 text-muted">
                      Prognostiziertes Kapital:{" "}
                      <span className="text-foreground">{formatCurrency(result.projectedCapital)}</span>
                    </p>
                    <p className="text-muted">
                      Kaufkraftbereinigt:{" "}
                      <span className="text-foreground">{formatCurrency(result.inflationAdjustedValue)}</span>
                    </p>
                    {result.goalReached && result.goalReachedDate && (
                      <p className="mt-1 text-positive">Ziel erreicht am {formatDate(result.goalReachedDate)}</p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          )}
        </Card>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Neues Szenario">
        <ScenarioForm onSuccess={() => setModalOpen(false)} />
      </Modal>
    </div>
  );
}
