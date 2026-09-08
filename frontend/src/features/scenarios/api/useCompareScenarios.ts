import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { ScenarioResult } from "../types/scenario";

export function useCompareScenarios(scenarioIds: string[]) {
  return useQuery({
    queryKey: ["scenario-compare", scenarioIds],
    queryFn: () => apiClient.get<ScenarioResult[]>(`/scenarios/compare?ids=${scenarioIds.join(",")}`),
    enabled: scenarioIds.length >= 2,
  });
}
