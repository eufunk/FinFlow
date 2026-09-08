"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useCreateScenario } from "../api/useCreateScenario";

const schema = z.object({
  name: z.string().min(1, "Bitte einen Namen eingeben").max(150, "Maximal 150 Zeichen"),
  currentCapital: z
    .string()
    .min(1, "Bitte einen Betrag eingeben")
    .refine((value) => Number(value) >= 0, "Darf nicht negativ sein"),
  monthlySavings: z
    .string()
    .min(1, "Bitte eine monatliche Sparrate eingeben")
    .refine((value) => Number(value) >= 0, "Darf nicht negativ sein"),
  annualReturn: z.string().min(1, "Bitte eine erwartete Rendite eingeben"),
  inflation: z.string().min(1, "Bitte eine Inflationsannahme eingeben"),
  durationInYears: z
    .string()
    .min(1, "Bitte eine Laufzeit eingeben")
    .refine((value) => Number(value) >= 1 && Number(value) <= 100, "Muss zwischen 1 und 100 Jahren liegen"),
  targetCapital: z.string().optional(),
});

type FormValues = z.infer<typeof schema>;

interface ScenarioFormProps {
  onSuccess: () => void;
}

export function ScenarioForm({ onSuccess }: ScenarioFormProps) {
  const createScenario = useCreateScenario();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { annualReturn: "4", inflation: "2", durationInYears: "20" },
  });

  const onSubmit = handleSubmit(async (values) => {
    await createScenario.mutateAsync({
      name: values.name,
      currentCapital: Number(values.currentCapital),
      monthlySavings: Number(values.monthlySavings),
      // Nutzer geben Rendite/Inflation in Prozent ein, das Backend erwartet einen Bruchteil
      annualReturn: Number(values.annualReturn) / 100,
      inflation: Number(values.inflation) / 100,
      durationInYears: Number(values.durationInYears),
      targetCapital: values.targetCapital ? Number(values.targetCapital) : undefined,
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
          placeholder="z. B. Current Plan"
          {...register("name")}
          className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
        />
        {errors.name && <p className="mt-1 text-xs text-negative">{errors.name.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="currentCapital">
            Startkapital (€)
          </label>
          <input
            id="currentCapital"
            type="number"
            step="0.01"
            {...register("currentCapital")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.currentCapital && <p className="mt-1 text-xs text-negative">{errors.currentCapital.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="monthlySavings">
            Monatliche Sparrate (€)
          </label>
          <input
            id="monthlySavings"
            type="number"
            step="0.01"
            {...register("monthlySavings")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.monthlySavings && <p className="mt-1 text-xs text-negative">{errors.monthlySavings.message}</p>}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="annualReturn">
            Erwartete Rendite (% p.a.)
          </label>
          <input
            id="annualReturn"
            type="number"
            step="0.1"
            {...register("annualReturn")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.annualReturn && <p className="mt-1 text-xs text-negative">{errors.annualReturn.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="inflation">
            Inflation (% p.a.)
          </label>
          <input
            id="inflation"
            type="number"
            step="0.1"
            {...register("inflation")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.inflation && <p className="mt-1 text-xs text-negative">{errors.inflation.message}</p>}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="durationInYears">
            Laufzeit (Jahre)
          </label>
          <input
            id="durationInYears"
            type="number"
            {...register("durationInYears")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
          {errors.durationInYears && <p className="mt-1 text-xs text-negative">{errors.durationInYears.message}</p>}
        </div>
        <div>
          <label className="block text-sm font-medium text-foreground" htmlFor="targetCapital">
            Zielkapital (€, optional)
          </label>
          <input
            id="targetCapital"
            type="number"
            step="0.01"
            {...register("targetCapital")}
            className="mt-1 w-full rounded-md border border-border px-3 py-2 text-sm"
          />
        </div>
      </div>

      {createScenario.isError && (
        <p className="text-sm text-negative">Szenario konnte nicht gespeichert werden. Bitte prüfe deine Eingaben.</p>
      )}

      <button
        type="submit"
        disabled={createScenario.isPending}
        className="w-full rounded-lg bg-primary py-2.5 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90 disabled:opacity-50"
      >
        {createScenario.isPending ? "Wird gespeichert …" : "Szenario speichern"}
      </button>
    </form>
  );
}
