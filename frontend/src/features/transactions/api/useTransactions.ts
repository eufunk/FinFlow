import { useQuery } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { Transaction } from "../types/transaction";

export function useTransactions() {
  return useQuery({
    queryKey: ["transactions"],
    queryFn: () => apiClient.get<Transaction[]>("/transactions"),
  });
}
