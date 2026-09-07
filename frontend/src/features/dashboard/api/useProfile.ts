import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { FinancialProfile } from "../types/profile";

export function useProfile() {
  return useQuery({
    queryKey: ["profile"],
    queryFn: () => apiClient.get<FinancialProfile>("/profile"),
  });
}
