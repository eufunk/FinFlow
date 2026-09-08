"use client";

import { useState } from "react";
import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { ErrorState } from "@/components/ui/ErrorState";
import { LoadingState } from "@/components/ui/LoadingState";
import { Modal } from "@/components/ui/Modal";
import { ProgressBar } from "@/components/ui/ProgressBar";
import { cn } from "@/lib/cn";
import { formatCurrency, formatDate, formatPercent } from "@/lib/format";
import { GoalForm } from "@/features/goals/components/GoalForm";
import { useGoals } from "@/features/goals/api/useGoals";

export default function GoalsPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const goalsQuery = useGoals();

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Sparziele</h1>
          <p className="mt-1 text-sm text-muted">Verfolge deinen Fortschritt zu deinen finanziellen Zielen.</p>
        </div>
        <button
          type="button"
          onClick={() => setModalOpen(true)}
          className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
        >
          + Neues Sparziel
        </button>
      </div>

      {goalsQuery.isLoading && <LoadingState label="Sparziele werden geladen …" />}
      {goalsQuery.isError && <ErrorState message="Sparziele konnten nicht geladen werden." />}
      {goalsQuery.data && goalsQuery.data.length === 0 && (
        <EmptyState title="Noch keine Sparziele" description="Lege dein erstes Sparziel über den Button oben an." />
      )}

      {goalsQuery.data && goalsQuery.data.length > 0 && (
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          {goalsQuery.data.map((goal) => {
            const progress = (goal.currentAmount / goal.targetAmount) * 100;
            return (
              <Card key={goal.id}>
                <div className="flex items-center justify-between">
                  <p className="font-medium text-foreground">{goal.name}</p>
                  {goal.achieved && <Badge variant="positive">Erreicht</Badge>}
                </div>

                <div className="mt-3">
                  <ProgressBar
                    value={progress}
                    variant={goal.achieved ? "positive" : "accent"}
                    valueLabel={`${formatCurrency(goal.currentAmount)} / ${formatCurrency(goal.targetAmount)}`}
                  />
                </div>

                <dl className="mt-4 grid grid-cols-2 gap-y-2 text-xs text-muted">
                  <dt>Monatliche Sparrate</dt>
                  <dd className="text-right text-foreground">{formatCurrency(goal.monthlyContribution)}</dd>
                  {goal.expectedAnnualReturn !== null && (
                    <>
                      <dt>Erwartete Rendite</dt>
                      <dd className="text-right text-foreground">{formatPercent(goal.expectedAnnualReturn)}</dd>
                    </>
                  )}
                  {goal.targetDate && (
                    <>
                      <dt>Zieldatum</dt>
                      <dd className="text-right text-foreground">{formatDate(goal.targetDate)}</dd>
                    </>
                  )}
                </dl>

                {!goal.achieved && (
                  <p className={cn("mt-3 text-xs", goal.estimatedAchievementDate ? "text-muted" : "text-negative")}>
                    {goal.estimatedAchievementDate
                      ? `Voraussichtlich erreicht am ${formatDate(goal.estimatedAchievementDate)}`
                      : "Mit aktueller Sparrate nicht erreichbar"}
                  </p>
                )}
              </Card>
            );
          })}
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Neues Sparziel">
        <GoalForm onSuccess={() => setModalOpen(false)} />
      </Modal>
    </div>
  );
}
