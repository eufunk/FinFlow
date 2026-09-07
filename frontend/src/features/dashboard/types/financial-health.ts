export interface CategoryScore {
  points: number;
  maxPoints: number;
  explanation: string;
}

export type RiskLevel = "LOW" | "MODERATE" | "ELEVATED" | "HIGH";

export interface FinancialHealthScore {
  totalScore: number;
  emergencyFund: CategoryScore;
  savingsRate: CategoryScore;
  debtRatio: CategoryScore;
  insuranceCoverage: CategoryScore;
  investmentDiversification: CategoryScore;
  recommendations: string[];
  riskLevel: RiskLevel;
}
