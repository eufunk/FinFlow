"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/cn";

const NAV_ITEMS = [
  { href: "/dashboard", label: "Dashboard" },
  { href: "/transactions", label: "Transaktionen" },
  { href: "/goals", label: "Sparziele" },
  { href: "/scenarios", label: "Szenarien" },
  { href: "/insurance", label: "Versicherungen" },
  { href: "/reports", label: "Berichte" },
  { href: "/settings", label: "Einstellungen" },
  { href: "/admin", label: "Admin" },
];

export function Navigation() {
  const pathname = usePathname();

  return (
    <header className="border-b border-border bg-surface">
      <div className="mx-auto flex max-w-6xl flex-wrap items-center gap-x-6 gap-y-2 px-6 py-4">
        <Link href="/dashboard" className="text-lg font-semibold text-primary">
          FinFlow
        </Link>
        <nav aria-label="Hauptnavigation">
          <ul className="flex flex-wrap gap-1 text-sm">
            {NAV_ITEMS.map((item) => {
              const active = pathname === item.href || pathname?.startsWith(`${item.href}/`);
              return (
                <li key={item.href}>
                  <Link
                    href={item.href}
                    aria-current={active ? "page" : undefined}
                    className={cn(
                      "rounded-md px-3 py-1.5 transition-colors",
                      active ? "bg-accent-soft text-accent" : "text-muted hover:bg-slate-100 hover:text-foreground",
                    )}
                  >
                    {item.label}
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
      </div>
    </header>
  );
}
