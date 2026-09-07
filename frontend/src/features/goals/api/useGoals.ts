import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { FinancialGoal } from "../types/goal";

export function useGoals() {
  return useQuery({
    queryKey: ["goals"],
    queryFn: () => apiClient.get<FinancialGoal[]>("/goals"),
  });
}
