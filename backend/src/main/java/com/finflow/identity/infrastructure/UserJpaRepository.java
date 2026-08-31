package com.finflow.identity.infrastructure;

import com.finflow.identity.domain.User;
import com.finflow.identity.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data liefert die Implementierung des UserRepository-Ports zur Laufzeit als Proxy -
 * Aufrufer (Application-Services) sind nur gegen das Port-Interface programmiert.
 */
@Repository
interface UserJpaRepository extends UserRepository, JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
