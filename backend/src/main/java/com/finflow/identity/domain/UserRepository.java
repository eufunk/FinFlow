package com.finflow.identity.domain;

import java.util.Optional;
import java.util.UUID;

/** Port: von application/domain genutzt, Implementierung liegt in infrastructure. */
public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    User save(User user);
}
