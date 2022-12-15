package com.jasik.momsnaggingapi.domain.follow.service;


import com.jasik.momsnaggingapi.domain.follow.Follow;
import com.jasik.momsnaggingapi.domain.follow.Follow.FollowResponse;
import com.jasik.momsnaggingapi.domain.follow.repository.FollowRepository;
import com.jasik.momsnaggingapi.domain.user.User;
import com.jasik.momsnaggingapi.domain.user.repository.UserRepository;
import com.jasik.momsnaggingapi.infra.common.ErrorCode;
import com.jasik.momsnaggingapi.infra.common.exception.NotValidException;
import com.jasik.momsnaggingapi.infra.common.exception.ScheduleNotFoundException;
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

    @Transactional
    public void postFollowing(User user, Long toUserId) {
        Optional<Follow> optional = followRepository.findByFromUserAndToUser(user.getId(), toUserId);
        if (optional.isPresent()) {
            throw new NotValidException("계정을 팔로우할 수 없습니다.",
                ErrorCode.FOLLOW_NOT_VALID);
        } else {
            Follow newFollow = Follow.builder().fromUser(user.getId()).toUser(toUserId).build();
            followRepository.save(newFollow);
        }
    }

    @Transactional
    public void deleteFollowing(User user, Long toUserId) {
        Optional<Follow> optional = followRepository.findByFromUserAndToUser(user.getId(), toUserId);
        if (optional.isPresent() && !optional.get().checkBlocked()) {
            followRepository.delete(optional.get());
        } else {
            throw new NotValidException("계정 팔로우 정보가 없습니다.",
                ErrorCode.FOLLOW_NOT_FOUND);
        }
    }
}
