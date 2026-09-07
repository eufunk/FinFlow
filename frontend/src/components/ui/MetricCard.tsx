import type { ReactNode } from "react";
import { Card } from "./Card";
import { cn } from "@/lib/cn";

interface MetricCardProps {
  label: string;
  value: string;
  hint?: ReactNode;
  trend?: "up" | "down" | "neutral";
}

export function MetricCard({ label, value, hint, trend = "neutral" }: MetricCardProps) {
  return (
    <Card>
      <p className="text-sm text-muted">{label}</p>
      <p className="mt-1 text-2xl font-semibold text-foreground">{value}</p>
      {hint && (
        <p
          className={cn(
            "mt-1 text-xs",
            trend === "up" && "text-positive",
            trend === "down" && "text-negative",
            trend === "neutral" && "text-muted",
          )}
        >
          {hint}
        </p>
      )}
    </Card>
  );
}
