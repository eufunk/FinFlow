import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { FinancialHealthScore } from "../types/financial-health";

export function useFinancialHealth() {
  return useQuery({
    queryKey: ["financial-health"],
    queryFn: () => apiClient.get<FinancialHealthScore>("/financial-health"),
  });
}
