package com.finflow.identity.application;

import java.util.UUID;

/**
 * Liefert die userId des aktuell angemeldeten Benutzers. Bis Phase 9 (Security/JWT) gibt es
 * eine befristete Implementierung (siehe identity.infrastructure.HeaderBasedCurrentUserProvider);
 * andere Module (z. B. financialprofile.api) programmieren ausschließlich gegen dieses Interface,
 * damit der spätere Wechsel auf JWT sie nicht betrifft.
 */
public interface CurrentUserProvider {

    UUID currentUserId();
}
