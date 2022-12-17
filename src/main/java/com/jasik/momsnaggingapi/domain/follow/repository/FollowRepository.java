package com.jasik.momsnaggingapi.domain.follow.repository;

import com.jasik.momsnaggingapi.domain.follow.Follow;
import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Transactional(readOnly = true)
    @Query(name = "findFollowers", nativeQuery = true)
    List<FollowResponse> findFollowersByUserId(
        @Param("userId") Long userId
    );

    @Transactional(readOnly = true)
    @Query(name = "findFollowings", nativeQuery = true)
    List<FollowResponse> findFollowingsByUserId(
        @Param("userId") Long userId
    );

    Optional<Follow> findByFromUserAndToUser(Long fromUserId, Long toUserId);
}
