"use client";

import { useState } from "react";
import { Badge } from "@/components/ui/Badge";
import { DataTable, type Column } from "@/components/ui/DataTable";
import { EmptyState } from "@/components/ui/EmptyState";
import { ErrorState } from "@/components/ui/ErrorState";
import { LoadingState } from "@/components/ui/LoadingState";
import { Modal } from "@/components/ui/Modal";
import { formatCurrency, formatDate } from "@/lib/format";
import { useCategories } from "@/features/transactions/api/useCategories";
import { useTransactions } from "@/features/transactions/api/useTransactions";
import { TransactionForm } from "@/features/transactions/components/TransactionForm";
import type { Transaction } from "@/features/transactions/types/transaction";

export default function TransactionsPage() {
  const [modalOpen, setModalOpen] = useState(false);
  const transactionsQuery = useTransactions();
  const categoriesQuery = useCategories();

  const categoryNameById = new Map((categoriesQuery.data ?? []).map((category) => [category.id, category.name]));
  const sortedTransactions = [...(transactionsQuery.data ?? [])].sort((a, b) =>
    b.bookedAt.localeCompare(a.bookedAt),
  );

  const columns: Column<Transaction>[] = [
    { key: "date", header: "Datum", cell: (row) => formatDate(row.bookedAt) },
    { key: "category", header: "Kategorie", cell: (row) => categoryNameById.get(row.categoryId) ?? "–" },
    { key: "description", header: "Beschreibung", cell: (row) => row.description ?? "–" },
    {
      key: "type",
      header: "Typ",
      cell: (row) => (
        <Badge variant={row.type === "INCOME" ? "positive" : "neutral"}>
          {row.type === "INCOME" ? "Einnahme" : "Ausgabe"}
        </Badge>
      ),
    },
    {
      key: "amount",
      header: "Betrag",
      className: "text-right",
      cell: (row) => (
        <span className={row.type === "INCOME" ? "font-medium text-positive" : "font-medium text-foreground"}>
          {row.type === "INCOME" ? "+" : "−"}
          {formatCurrency(row.amount)}
        </span>
      ),
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-semibold text-foreground">Transaktionen</h1>
          <p className="mt-1 text-sm text-muted">Alle erfassten Einnahmen und Ausgaben.</p>
        </div>
        <button
          type="button"
          onClick={() => setModalOpen(true)}
          className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
        >
          + Neue Transaktion
        </button>
      </div>

      {transactionsQuery.isLoading && <LoadingState label="Transaktionen werden geladen …" />}
      {transactionsQuery.isError && <ErrorState message="Transaktionen konnten nicht geladen werden." />}
      {transactionsQuery.data && sortedTransactions.length === 0 && (
        <EmptyState
          title="Noch keine Transaktionen"
          description="Erfasse deine erste Einnahme oder Ausgabe über den Button oben."
        />
      )}
      {sortedTransactions.length > 0 && (
        <DataTable columns={columns} data={sortedTransactions} rowKey={(row) => row.id} />
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Neue Transaktion">
        <TransactionForm onSuccess={() => setModalOpen(false)} />
      </Modal>
    </div>
  );
}
