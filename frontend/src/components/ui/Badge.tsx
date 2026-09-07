import type { ReactNode } from "react";
import { cn } from "@/lib/cn";

export type BadgeVariant = "neutral" | "positive" | "negative" | "warning" | "accent";

const VARIANT_CLASSES: Record<BadgeVariant, string> = {
  neutral: "bg-slate-100 text-slate-700",
  positive: "bg-positive-soft text-positive",
  negative: "bg-negative-soft text-negative",
  warning: "bg-warning-soft text-warning",
  accent: "bg-accent-soft text-accent",
};

interface BadgeProps {
  variant?: BadgeVariant;
  children: ReactNode;
}

export function Badge({ variant = "neutral", children }: BadgeProps) {
  return (
    <span
      className={cn(
        "inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium",
        VARIANT_CLASSES[variant],
      )}
    >
      {children}
    </span>
  );
}
