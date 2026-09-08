"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { LoadingState } from "@/components/ui/LoadingState";
import { useCategories } from "../api/useCategories";
import { useCreateTransaction } from "../api/useCreateTransaction";

const schema = z
  .object({
    // bleibt bewusst ein String (nicht z.coerce.number()): sonst haben Input- und Output-Typ
    // des Schemas unterschiedliche Typen, was mit React Hook Forms useForm<T> kollidiert.
    // Die Umwandlung zu number passiert explizit beim Absenden.
    amount: z
      .string()
      .min(1, "Bitte einen Betrag eingeben")
      .refine((value) => Number(value) > 0, "Der Betrag muss größer als 0 sein"),
    type: z.enum(["INCOME", "EXPENSE"]),
    categoryId: z.string().min(1, "Bitte eine Kategorie wählen"),
    bookedAt: z.string().min(1, "Bitte ein Datum wählen"),
    description: z.string().max(255, "Maximal 255 Zeichen").optional(),
  })
  .refine((data) => new Date(data.bookedAt) <= new Date(), {
    message: "Das Datum darf nicht in der Zukunft liegen",
    path: ["bookedAt"],
  });

type FormValues = z.infer<typeof schema>;

interface TransactionFormProps {
  onSuccess: () => void;
}

export function TransactionForm({ onSuccess }: TransactionFormProps) {
  const categoriesQuery = useCategories();
  const createTransaction = useCreateTransaction();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      type: "EXPENSE",
      bookedAt: new Date().toISOString().slice(0, 10),
    },
  });

  const selectedType = watch("type");
  const filteredCategories = (categoriesQuery.data ?? []).filter(
    (category) => category.type === selectedType,
  );

  const onSubmit = handleSubmit(async (values) => {
    await createTransaction.mutateAsync({
      amount: Number(values.amount),
      type: values.type,
      categoryId: values.categoryId,
      bookedAt: values.bookedAt,
      description: values.description || undefined,
    });
    onSuccess();
  });

  if (categoriesQuery.isLoading) {
    return <LoadingState label="Kategorien werden geladen …" />;
  }

  return (
    <form onSubmit={onSubmit} className="space-y-4" noValidate>
      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="type">
          Typ
        </label>
        <select
          id="type"
          {...register("type")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        >
          <option value="EXPENSE">Ausgabe</option>
          <option value="INCOME">Einnahme</option>
        </select>
      </div>

      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="categoryId">
          Kategorie
        </label>
        <select
          id="categoryId"
          {...register("categoryId")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        >
          <option value="">Bitte wählen …</option>
          {filteredCategories.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        {errors.categoryId && <p className="mt-1 text-xs text-negative">{errors.categoryId.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="amount">
          Betrag (€)
        </label>
        <input
          id="amount"
          type="number"
          step="0.01"
          {...register("amount")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.amount && <p className="mt-1 text-xs text-negative">{errors.amount.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="bookedAt">
          Datum
        </label>
        <input
          id="bookedAt"
          type="date"
          {...register("bookedAt")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.bookedAt && <p className="mt-1 text-xs text-negative">{errors.bookedAt.message}</p>}
      </div>

      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="description">
          Beschreibung (optional)
        </label>
        <input
          id="description"
          type="text"
          {...register("description")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.description && <p className="mt-1 text-xs text-negative">{errors.description.message}</p>}
      </div>

      {createTransaction.isError && (
        <p className="text-sm text-negative">
          Transaktion konnte nicht gespeichert werden. Bitte prüfe deine Eingaben.
        </p>
      )}

      <button
        type="submit"
        disabled={createTransaction.isPending}
        className="w-full rounded-lg bg-primary py-2.5 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90 disabled:opacity-50"
      >
        {createTransaction.isPending ? "Wird gespeichert …" : "Transaktion speichern"}
      </button>
    </form>
  );
}
