"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useCreateGoal } from "../api/useCreateGoal";

// Beträge/Prozentsätze bleiben im Formular Strings (siehe TransactionForm) - z.coerce.number()
// erzeugt einen Input-/Output-Typ-Konflikt mit React Hook Forms useForm<T>.
const schema = z
  .object({
    name: z.string().min(1, "Bitte einen Namen eingeben").max(150, "Maximal 150 Zeichen"),
    targetAmount: z
      .string()
      .min(1, "Bitte einen Zielbetrag eingeben")
      .refine((value) => Number(value) > 0, "Der Zielbetrag muss größer als 0 sein"),
    currentAmount: z
      .string()
      .min(1, "Bitte einen Betrag eingeben")
      .refine((value) => Number(value) >= 0, "Darf nicht negativ sein"),
    targetDate: z.string().optional(),
    monthlyContribution: z
      .string()
      .min(1, "Bitte eine monatliche Sparrate eingeben")
      .refine((value) => Number(value) >= 0, "Darf nicht negativ sein"),
    expectedAnnualReturn: z.string().optional(),
  })
  .refine(
    (data) => !data.targetDate || new Date(data.targetDate) >= new Date(new Date().toDateString()),
    { message: "Das Zieldatum darf nicht in der Vergangenheit liegen", path: ["targetDate"] },
  );

type FormValues = z.infer<typeof schema>;

interface GoalFormProps {
  onSuccess: () => void;
}

export function GoalForm({ onSuccess }: GoalFormProps) {
  const createGoal = useCreateGoal();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { currentAmount: "0", monthlyContribution: "0" },
  });

  const onSubmit = handleSubmit(async (values) => {
    await createGoal.mutateAsync({
      name: values.name,
      targetAmount: Number(values.targetAmount),
      currentAmount: Number(values.currentAmount),
      targetDate: values.targetDate || undefined,
      monthlyContribution: Number(values.monthlyContribution),
      // Nutzer geben die Rendite als Prozentzahl ein (z. B. 4), das Backend erwartet einen Bruchteil (0.04)
      expectedAnnualReturn: values.expectedAnnualReturn ? Number(values.expectedAnnualReturn) / 100 : undefined,
    });
    onSuccess();
  });

  return (
    <form onSubmit={onSubmit} className="space-y-4" noValidate>
      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="name">
          Name
        </label>
        <input
          id="name"
          type="text"
          {...register("name")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.name && <p className="mt-1 text-xs text-negative">{errors.name.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="targetAmount">
            Zielbetrag (€)
          </label>
          <input
            id="targetAmount"
            type="number"
            step="0.01"
            {...register("targetAmount")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.targetAmount && <p className="mt-1 text-xs text-negative">{errors.targetAmount.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="currentAmount">
            Bereits gespart (€)
          </label>
          <input
            id="currentAmount"
            type="number"
            step="0.01"
            {...register("currentAmount")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.currentAmount && <p className="mt-1 text-xs text-negative">{errors.currentAmount.message}</p>}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="monthlyContribution">
            Monatliche Sparrate (€)
          </label>
          <input
            id="monthlyContribution"
            type="number"
            step="0.01"
            {...register("monthlyContribution")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.monthlyContribution && (
            <p className="mt-1 text-xs text-negative">{errors.monthlyContribution.message}</p>
          )}
        </div>
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="expectedAnnualReturn">
            Erwartete Rendite (% p.a., optional)
          </label>
          <input
            id="expectedAnnualReturn"
            type="number"
            step="0.1"
            {...register("expectedAnnualReturn")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
        </div>
      </div>

      <div>
        <label className="block text-sm font-medium text-foreground" htmlFor="targetDate">
          Zieldatum (optional)
        </label>
        <input
          id="targetDate"
          type="date"
          {...register("targetDate")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.targetDate && <p className="mt-1 text-xs text-negative">{errors.targetDate.message}</p>}
      </div>

      {createGoal.isError && (
        <p className="text-sm text-negative">Sparziel konnte nicht gespeichert werden. Bitte prüfe deine Eingaben.</p>
      )}

      <button
        type="submit"
        disabled={createGoal.isPending}
        className="w-full rounded-lg bg-primary py-2.5 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90 disabled:opacity-50"
      >
        {createGoal.isPending ? "Wird gespeichert …" : "Sparziel speichern"}
      </button>
    </form>
  );
}
