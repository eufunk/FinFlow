import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { ScenarioResult } from "../types/scenario";

export function useScenarioResult(scenarioId: string | undefined) {
  return useQuery({
    queryKey: ["scenario-result", scenarioId],
    queryFn: () => apiClient.get<ScenarioResult>(`/scenarios/${scenarioId}/result`),
    enabled: Boolean(scenarioId),
  });
}
