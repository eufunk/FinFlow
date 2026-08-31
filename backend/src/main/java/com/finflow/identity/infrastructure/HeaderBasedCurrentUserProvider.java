package com.finflow.identity.infrastructure;

import com.finflow.identity.application.CurrentUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * TEMPORÄR bis Phase 9 (Security/JWT): liest die User-ID aus dem Header "X-User-Id" statt aus
 * einem echten Security-Context. Wird durch eine JWT-basierte Implementierung ersetzt, sobald
 * Spring Security eingeführt ist - Controller/Service ändern sich dabei nicht, da sie nur gegen
 * CurrentUserProvider programmiert sind.
 */
@Component
class HeaderBasedCurrentUserProvider implements CurrentUserProvider {

    static final String HEADER_NAME = "X-User-Id";

    private final HttpServletRequest request;

    HeaderBasedCurrentUserProvider(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public UUID currentUserId() {
        String header = request.getHeader(HEADER_NAME);
        if (header == null || header.isBlank()) {
            throw new IllegalStateException(
                    "Missing " + HEADER_NAME + " header (temporary stand-in until Phase 9 security is implemented)");
        }
        try {
            return UUID.fromString(header);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(HEADER_NAME + " header is not a valid UUID: " + header, e);
        }
    }
}
