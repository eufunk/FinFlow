"use client";

import { MetricCard } from "@/components/ui/MetricCard";
import { LoadingState } from "@/components/ui/LoadingState";
import { ErrorState } from "@/components/ui/ErrorState";
import { EmptyState } from "@/components/ui/EmptyState";
import { ApiError } from "@/lib/api-client";
import { formatCurrency, formatPercent } from "@/lib/format";
import { useAccounts } from "@/features/dashboard/api/useAccounts";
import { useFinancialHealth } from "@/features/dashboard/api/useFinancialHealth";
import { useProfile } from "@/features/dashboard/api/useProfile";
import { FinancialHealthCard } from "@/features/dashboard/components/FinancialHealthCard";
import { GoalsSummaryCard } from "@/features/dashboard/components/GoalsSummaryCard";
import { RecommendationsCard } from "@/features/dashboard/components/RecommendationsCard";
import { WealthDevelopmentCard } from "@/features/dashboard/components/WealthDevelopmentCard";
import { useGoals } from "@/features/goals/api/useGoals";

export default function DashboardPage() {
  const profileQuery = useProfile();
  const healthQuery = useFinancialHealth();
  const accountsQuery = useAccounts();
  const goalsQuery = useGoals();

  const profileMissing =
    profileQuery.error instanceof ApiError && profileQuery.error.status === 404;

  if (profileQuery.isLoading) {
    return <LoadingState label="Dashboard wird geladen …" />;
  }

  if (profileMissing) {
    return (
      <EmptyState
        title="Noch kein Finanzprofil hinterlegt"
        description="Hinterlege dein monatliches Einkommen und deine Ausgaben, damit FinFlow deine Kennzahlen berechnen kann."
      />
    );
  }

  if (profileQuery.isError) {
    return <ErrorState message="Dein Finanzprofil konnte nicht geladen werden." />;
  }

  const profile = profileQuery.data!;
  const netWorth = accountsQuery.data?.reduce((sum, account) => sum + account.balance, 0) ?? null;
  const savingsRate =
    profile.monthlyIncome > 0
      ? (profile.monthlyIncome - profile.monthlyExpenses) / profile.monthlyIncome
      : null;

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Dashboard</h1>
        <p className="mt-1 text-sm text-muted">Deine finanzielle Situation auf einen Blick.</p>
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <MetricCard
          label="Net Worth"
          value={netWorth === null ? "–" : formatCurrency(netWorth)}
          hint={accountsQuery.isLoading ? "Wird geladen …" : `${accountsQuery.data?.length ?? 0} Konten`}
        />
        <MetricCard label="Monatliches Einkommen" value={formatCurrency(profile.monthlyIncome)} />
        <MetricCard label="Monatliche Ausgaben" value={formatCurrency(profile.monthlyExpenses)} />
        <MetricCard
          label="Sparquote"
          value={savingsRate === null ? "–" : formatPercent(savingsRate)}
          trend={savingsRate === null ? "neutral" : savingsRate >= 0.2 ? "up" : savingsRate < 0 ? "down" : "neutral"}
          hint="Ziel: mindestens 20 %"
        />
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
        <div className="lg:col-span-2 space-y-6">
          {healthQuery.isLoading && <LoadingState label="Financial Health Score wird berechnet …" />}
          {healthQuery.isError && <ErrorState message="Der Financial Health Score konnte nicht berechnet werden." />}
          {healthQuery.data && <FinancialHealthCard score={healthQuery.data} />}

          <WealthDevelopmentCard />
        </div>

        <div className="space-y-6">
          {healthQuery.data && <RecommendationsCard recommendations={healthQuery.data.recommendations} />}

          {goalsQuery.isLoading && <LoadingState label="Sparziele werden geladen …" />}
          {goalsQuery.isError && <ErrorState message="Sparziele konnten nicht geladen werden." />}
          {goalsQuery.data && <GoalsSummaryCard goals={goalsQuery.data} />}
        </div>
      </div>
    </div>
  );
}
