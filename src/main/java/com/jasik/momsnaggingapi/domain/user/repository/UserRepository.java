package com.jasik.momsnaggingapi.domain.user.repository;

import com.jasik.momsnaggingapi.domain.user.Interface.UserFollowInterface;
import com.jasik.momsnaggingapi.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPersonalId(String personalId);
    Optional<User> findByProviderCode(String ProviderCode);
    Optional<User> findByProviderCodeAndProvider(String providerCode, String provider);
    Page<User> findByPersonalIdContainingIgnoreCase(String personalId, Pageable pageable);
    @Transactional
    @Query("select u as user, f as follow from User u left outer join Follow f " +
            "on f.fromUser = :id and u.id = f.toUser " +
            "where u.id <> :id and u.personalId LIKE %:personalId%")
    List<UserFollowInterface> findAllByPersonalIdContainingIgnoreCaseAndIdNot(@Param("id") Long id, @Param("personalId") String personalId);
}
