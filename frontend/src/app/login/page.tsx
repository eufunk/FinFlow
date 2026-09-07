import Link from "next/link";
import { Card } from "@/components/ui/Card";
import { Alert } from "@/components/ui/Alert";

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center p-6">
      <Card className="w-full max-w-sm">
        <h1 className="text-xl font-semibold text-foreground">Bei FinFlow anmelden</h1>
        <div className="mt-4">
          <Alert variant="info" title="Login noch nicht implementiert">
            Die echte Authentifizierung (JWT, Rollen) kommt erst in Phase 9. Bis dahin arbeitet
            die Anwendung automatisch mit einem festen Demo-Nutzer.
          </Alert>
        </div>
        <Link
          href="/dashboard"
          className="mt-6 block w-full rounded-lg bg-primary py-2.5 text-center text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
        >
          Weiter zum Dashboard
        </Link>
      </Card>
    </div>
  );
}
