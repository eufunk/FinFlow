import { Badge, type BadgeVariant } from "@/components/ui/Badge";
import { Card } from "@/components/ui/Card";
import { ProgressBar, type ProgressBarVariant } from "@/components/ui/ProgressBar";
import type { CategoryScore, FinancialHealthScore, RiskLevel } from "../types/financial-health";

const RISK_LEVEL_LABEL: Record<RiskLevel, string> = {
  LOW: "Niedriges Risiko",
  MODERATE: "Moderates Risiko",
  ELEVATED: "Erhöhtes Risiko",
  HIGH: "Hohes Risiko",
};

const RISK_LEVEL_VARIANT: Record<RiskLevel, BadgeVariant> = {
  LOW: "positive",
  MODERATE: "accent",
  ELEVATED: "warning",
  HIGH: "negative",
};

const CATEGORY_LABELS: Record<CategoryKey, string> = {
  emergencyFund: "Notgroschen",
  savingsRate: "Sparquote",
  debtRatio: "Schuldenquote",
  insuranceCoverage: "Versicherungsschutz",
  investmentDiversification: "Investment & Diversifikation",
};

type CategoryKey =
  | "emergencyFund"
  | "savingsRate"
  | "debtRatio"
  | "insuranceCoverage"
  | "investmentDiversification";

function categoryVariant(category: CategoryScore): ProgressBarVariant {
  const ratio = category.points / category.maxPoints;
  if (ratio >= 0.75) return "positive";
  if (ratio >= 0.4) return "warning";
  return "negative";
}

export function FinancialHealthCard({ score }: { score: FinancialHealthScore }) {
  const categories: Array<[CategoryKey, CategoryScore]> = [
    ["emergencyFund", score.emergencyFund],
    ["savingsRate", score.savingsRate],
    ["debtRatio", score.debtRatio],
    ["insuranceCoverage", score.insuranceCoverage],
    ["investmentDiversification", score.investmentDiversification],
  ];

  return (
    <Card>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm text-muted">Financial Health Score</p>
          <p className="mt-1 text-4xl font-semibold text-foreground">
            {score.totalScore}
            <span className="text-lg text-muted">/100</span>
          </p>
        </div>
        <Badge variant={RISK_LEVEL_VARIANT[score.riskLevel]}>{RISK_LEVEL_LABEL[score.riskLevel]}</Badge>
      </div>
      <div className="mt-5 space-y-3">
        {categories.map(([key, category]) => (
          <ProgressBar
            key={key}
            label={CATEGORY_LABELS[key]}
            value={(category.points / category.maxPoints) * 100}
            valueLabel={`${category.points}/${category.maxPoints}`}
            variant={categoryVariant(category)}
          />
        ))}
      </div>
    </Card>
  );
}
