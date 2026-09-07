import { apiClient, setCurrentUserId } from "./api-client";

const STORAGE_KEY = "finflow.currentUserId";

interface DemoUserResponse {
  userId: string;
}

/**
 * TEMPORÄR bis Phase 9 (Security/JWT): es gibt noch kein Login, daher wird beim App-Start einmalig
 * die ID des Backend-Demo-Users (siehe DevDataSeeder) aufgelöst und für alle folgenden Requests
 * als X-User-Id-Header verwendet. Wird durch einen echten Login-Flow ersetzt.
 */
export async function resolveCurrentUserId(): Promise<string> {
  if (typeof window !== "undefined") {
    const cached = window.localStorage.getItem(STORAGE_KEY);
    if (cached) {
      setCurrentUserId(cached);
      return cached;
    }
  }

  const { userId } = await apiClient.get<DemoUserResponse>("/dev/demo-user", { skipUserHeader: true });
  setCurrentUserId(userId);
  if (typeof window !== "undefined") {
    window.localStorage.setItem(STORAGE_KEY, userId);
  }
  return userId;
}
