import { EmptyState } from "@/components/ui/EmptyState";

interface ComingSoonProps {
  title: string;
  description: string;
}

export function ComingSoon({ title, description }: ComingSoonProps) {
  return (
    <div>
      <h1 className="text-2xl font-semibold text-foreground">{title}</h1>
      <div className="mt-6">
        <EmptyState title="Diese Seite ist noch nicht fertig" description={description} />
      </div>
    </div>
  );
}
