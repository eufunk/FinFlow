export interface Scenario {
  id: string;
  name: string;
  currentCapital: number;
  monthlySavings: number;
  annualReturn: number;
  inflation: number;
  durationInYears: number;
  monthlyIncome: number | null;
  incomeGrowth: number | null;
  expensesGrowth: number | null;
  targetCapital: number | null;
  createdAt: string;
}

export interface YearlySnapshot {
  year: number;
  capital: number;
}

export interface ScenarioResult {
  scenarioId: string;
  scenarioName: string;
  projectedCapital: number;
  yearlyDevelopment: YearlySnapshot[];
  totalContributions: number;
  investmentGrowth: number;
  inflationAdjustedValue: number;
  goalReached: boolean;
  goalReachedDate: string | null;
}
