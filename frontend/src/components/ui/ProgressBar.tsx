import { cn } from "@/lib/cn";

export type ProgressBarVariant = "accent" | "positive" | "warning" | "negative";

const BAR_COLOR: Record<ProgressBarVariant, string> = {
  accent: "bg-accent",
  positive: "bg-positive",
  warning: "bg-warning",
  negative: "bg-negative",
};

interface ProgressBarProps {
  value: number;
  variant?: ProgressBarVariant;
  label?: string;
  valueLabel?: string;
}

export function ProgressBar({ value, variant = "accent", label, valueLabel }: ProgressBarProps) {
  const clamped = Math.min(100, Math.max(0, value));
  return (
    <div>
      {(label || valueLabel) && (
        <div className="mb-1 flex justify-between text-xs text-muted">
          <span>{label}</span>
          <span>{valueLabel ?? `${clamped.toFixed(0)}%`}</span>
        </div>
      )}
      <div className="h-2 w-full overflow-hidden rounded-full bg-slate-100">
        <div
          className={cn("h-full rounded-full transition-all", BAR_COLOR[variant])}
          style={{ width: `${clamped}%` }}
        />
      </div>
    </div>
  );
}
