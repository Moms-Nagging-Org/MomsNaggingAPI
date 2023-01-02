package com.jasik.momsnaggingapi.domain.user.repository;

import com.jasik.momsnaggingapi.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPersonalId(String personalId);
    Optional<User> findByProviderCode(String ProviderCode);
    Optional<User> findByProviderCodeAndProvider(String providerCode, String provider);
    Page<User> findByPersonalIdContainingIgnoreCase(String personalId, Pageable pageable);
    List<User> findAllByPersonalIdContainingIgnoreCase(String personalId);
}
