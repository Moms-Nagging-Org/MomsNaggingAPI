package com.jasik.momsnaggingapi.domain.user.repository;

import com.jasik.momsnaggingapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndProvider(String email, String provider);
    Optional<User> findByPersonalId(String personalId);
    Optional<User> findByProviderCode(String ProviderCode);
    Optional<User> findByProviderCodeAndProvider(String providerCode, String provider);
}
