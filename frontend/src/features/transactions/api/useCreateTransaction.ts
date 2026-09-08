import { useMutation, useQueryClient } from "@tanstack/react-query";
import { apiClient } from "@/lib/api-client";
import type { Transaction, TransactionType } from "../types/transaction";

export interface CreateTransactionInput {
  amount: number;
  type: TransactionType;
  categoryId: string;
  bookedAt: string;
  description?: string;
}

export function useCreateTransaction() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (input: CreateTransactionInput) => apiClient.post<Transaction>("/transactions", input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
    },
  });
}
