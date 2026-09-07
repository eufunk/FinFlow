interface LoadingStateProps {
  label?: string;
}

export function LoadingState({ label = "Wird geladen …" }: LoadingStateProps) {
  return (
    <div className="flex items-center justify-center gap-2 p-10 text-sm text-muted">
      <span className="h-4 w-4 animate-spin rounded-full border-2 border-accent border-t-transparent" />
      {label}
    </div>
  );
}
