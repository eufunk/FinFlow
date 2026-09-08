import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { Scenario } from "../types/scenario";

export interface CreateScenarioInput {
  name: string;
  currentCapital: number;
  monthlySavings: number;
  annualReturn: number;
  inflation: number;
  durationInYears: number;
  monthlyIncome?: number;
  incomeGrowth?: number;
  expensesGrowth?: number;
  targetCapital?: number;
}

export function useCreateScenario() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: CreateScenarioInput) => apiClient.post<Scenario>("/scenarios", input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["scenarios"] });
    },
  });
}
