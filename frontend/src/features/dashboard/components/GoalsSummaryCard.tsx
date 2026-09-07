import { Badge } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { EmptyState } from "@/components/ui/EmptyState";
import { ProgressBar } from "@/components/ui/ProgressBar";
import { formatCurrency, formatDate } from "@/lib/format";
import type { FinancialGoal } from "@/features/goals/types/goal";

export function GoalsSummaryCard({ goals }: { goals: FinancialGoal[] }) {
  if (goals.length === 0) {
    return (
      <EmptyState
        title="Noch keine Sparziele"
        description="Lege dein erstes Sparziel an, um deinen Fortschritt hier zu verfolgen."
      />
    );
  }

  return (
    <Card>
      <p className="text-sm font-medium text-foreground">Sparziele</p>
      <div className="mt-4 space-y-4">
        {goals.map((goal) => {
          const progress = (goal.currentAmount / goal.targetAmount) * 100;
          return (
            <div key={goal.id}>
              <div className="flex items-center justify-between text-sm">
                <span className="font-medium text-foreground">{goal.name}</span>
                {goal.achieved && <Badge variant="positive">Erreicht</Badge>}
              </div>
              <div className="mt-1">
                <ProgressBar
                  value={progress}
                  variant={goal.achieved ? "positive" : "accent"}
                  valueLabel={`${formatCurrency(goal.currentAmount)} / ${formatCurrency(goal.targetAmount)}`}
                />
              </div>
              {!goal.achieved && goal.estimatedAchievementDate && (
                <p className="mt-1 text-xs text-muted">
                  Voraussichtlich erreicht am {formatDate(goal.estimatedAchievementDate)}
                </p>
              )}
              {!goal.achieved && !goal.estimatedAchievementDate && (
                <p className="mt-1 text-xs text-negative">Mit aktueller Sparrate nicht erreichbar</p>
              )}
            </div>
          );
        })}
      </div>
    </Card>
  );
}
