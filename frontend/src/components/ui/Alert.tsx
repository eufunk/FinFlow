import type { ReactNode } from "react";
import { cn } from "@/lib/cn";

export type AlertVariant = "info" | "warning" | "error" | "success";

const VARIANT_CLASSES: Record<AlertVariant, string> = {
  info: "border-accent/30 bg-accent-soft text-accent",
  warning: "border-warning/30 bg-warning-soft text-warning",
  error: "border-negative/30 bg-negative-soft text-negative",
  success: "border-positive/30 bg-positive-soft text-positive",
};

interface AlertProps {
  variant?: AlertVariant;
  title?: string;
  children: ReactNode;
}

export function Alert({ variant = "info", title, children }: AlertProps) {
  return (
    <div className={cn("rounded-lg border p-4 text-sm", VARIANT_CLASSES[variant])}>
      {title && <p className="font-medium">{title}</p>}
      <div className={cn("text-foreground/80", title && "mt-1")}>{children}</div>
    </div>
  );
}
