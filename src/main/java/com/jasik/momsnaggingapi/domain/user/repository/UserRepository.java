package com.jasik.momsnaggingapi.domain.user.repository;

import com.jasik.momsnaggingapi.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
