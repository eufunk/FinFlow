export type AccountType = "CHECKING" | "SAVINGS" | "INVESTMENT" | "CASH";

export interface Account {
  id: string;
  name: string;
  type: AccountType;
  balance: number;
  updatedAt: string;
}
