package com.finflow.shared.dev;

import com.finflow.identity.domain.UserRepository;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Ersetzt bis Phase 9 (Security/JWT) den Login: liefert die ID des von {@link DevDataSeeder}
 * angelegten Demo-Users, damit das Frontend weiß, als wer es Requests stellen soll (X-User-Id
 * Header). Nur unter Profil "dev" registriert - existiert in Produktion nicht.
 */
@RestController
@RequestMapping("/api/v1/dev")
@Profile("dev")
class DevUserController {

    private final UserRepository userRepository;

    DevUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/demo-user")
    ResponseEntity<DemoUserResponse> demoUser() {
        return userRepository.findByEmail(DevDataSeeder.DEMO_USER_EMAIL)
                .map(user -> ResponseEntity.ok(new DemoUserResponse(user.id())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    record DemoUserResponse(UUID userId) {
    }
}
