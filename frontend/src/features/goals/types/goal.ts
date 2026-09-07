export interface FinancialGoal {
  id: string;
  name: string;
  targetAmount: number;
  currentAmount: number;
  targetDate: string | null;
  monthlyContribution: number;
  expectedAnnualReturn: number | null;
  achieved: boolean;
  estimatedAchievementDate: string | null;
  createdAt: string;
  updatedAt: string;
}
