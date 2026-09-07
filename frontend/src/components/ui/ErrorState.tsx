import { Alert } from "./Alert";

interface ErrorStateProps {
  message?: string;
}

export function ErrorState({ message = "Etwas ist schiefgelaufen. Bitte versuche es später erneut." }: ErrorStateProps) {
  return (
    <Alert variant="error" title="Fehler beim Laden">
      {message}
    </Alert>
  );
}
