import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { Scenario } from "../types/scenario";

export function useScenarios() {
  return useQuery({
    queryKey: ["scenarios"],
    queryFn: () => apiClient.get<Scenario[]>("/scenarios"),
  });
}
