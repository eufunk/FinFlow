"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { resolveCurrentUserId } from "@/lib/current-user";

type BootStatus = "pending" | "ready" | "error";

export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 30_000,
            retry: 1,
          },
        },
      }),
  );
  const [status, setStatus] = useState<BootStatus>("pending");

  useEffect(() => {
    let cancelled = false;
    resolveCurrentUserId()
      .then(() => {
        if (!cancelled) setStatus("ready");
      })
      .catch(() => {
        if (!cancelled) setStatus("error");
      });
    return () => {
      cancelled = true;
    };
  }, []);

  if (status === "pending") {
    return (
      <div className="flex min-h-screen items-center justify-center text-sm text-muted">
        FinFlow wird geladen …
      </div>
    );
  }

  if (status === "error") {
    return (
      <div className="flex min-h-screen items-center justify-center p-6">
        <div className="max-w-md rounded-lg border border-negative/30 bg-negative-soft p-6 text-center">
          <p className="font-medium text-negative">Verbindung zum Backend fehlgeschlagen</p>
          <p className="mt-2 text-sm text-muted">
            Läuft das Backend auf http://localhost:8080 und ist das Spring-Profil{" "}
            <code className="font-mono">dev</code> aktiv? Ohne dieses Profil existiert kein
            Demo-User, gegen den das Frontend arbeiten kann (siehe Docs/Umsetzungsvortschritt.md).
          </p>
        </div>
      </div>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
}
