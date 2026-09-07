import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { Account } from "../types/account";

export function useAccounts() {
  return useQuery({
    queryKey: ["accounts"],
    queryFn: () => apiClient.get<Account[]>("/accounts"),
  });
}
