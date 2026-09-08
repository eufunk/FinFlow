export type TransactionType = "INCOME" | "EXPENSE";
export type TransactionSource = "MANUAL" | "JSON_IMPORT" | "XML_IMPORT";

export interface Transaction {
  id: string;
  categoryId: string;
  amount: number;
  type: TransactionType;
  bookedAt: string;
  description: string | null;
  source: TransactionSource;
  createdAt: string;
}

export interface Category {
  id: string;
  name: string;
  type: TransactionType;
}
