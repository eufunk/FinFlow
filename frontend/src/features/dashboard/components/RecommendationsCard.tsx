import { Alert } from "@/components/ui/Alert";
import { Card } from "@/components/ui/Card";

export function RecommendationsCard({ recommendations }: { recommendations: string[] }) {
  if (recommendations.length === 0) {
    return (
      <Alert variant="success" title="Alles im grünen Bereich">
        Aktuell gibt es keine dringenden Handlungsempfehlungen.
      </Alert>
    );
  }

  return (
    <Card>
      <p className="text-sm font-medium text-foreground">Empfehlungen</p>
      <ul className="mt-3 space-y-2 text-sm text-muted">
        {recommendations.map((recommendation) => (
          <li key={recommendation} className="flex gap-2">
            <span className="text-warning" aria-hidden="true">
              •
            </span>
            <span>{recommendation}</span>
          </li>
        ))}
      </ul>
    </Card>
  );
}
