package com.jasik.momsnaggingapi.domain.follow.service;


import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.domain.follow.repository.FollowRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowers(Long userId) {

        return followRepository.findFollowersByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<FollowResponse> getFollowings(Long userId) {

        return followRepository.findFollowingsByUserId(userId);
    }
}
